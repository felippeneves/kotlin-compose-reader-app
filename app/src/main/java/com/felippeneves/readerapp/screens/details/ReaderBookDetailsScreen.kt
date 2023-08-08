package com.felippeneves.readerapp.screens.details

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.felippeneves.readerapp.R
import com.felippeneves.readerapp.components.Loading
import com.felippeneves.readerapp.components.ReaderAppBar
import com.felippeneves.readerapp.components.RoundedButton
import com.felippeneves.readerapp.data.Resource
import com.felippeneves.readerapp.model.Item
import com.felippeneves.readerapp.model.MBook
import com.felippeneves.readerapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderBookDetailsScreen(
    navController: NavController,
    bookId: String,
    viewModel: ReaderBookDetailsViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = stringResource(id = R.string.book_details),
            icon = Icons.Default.ArrowBack,
            navController = navController,
        ) {
            navController.popBackStack()
        }
    }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId = bookId)
                }.value

                if (bookInfo.data == null) {
                    Loading()
                } else {
                    ShowBookDetails(bookInfo = bookInfo, navController = navController)
                }
            }
        }
    }
}

@Composable
fun ShowBookDetails(
    bookInfo: Resource<Item>,
    navController: NavController
) {
    val bookData = bookInfo.data?.volumeInfo!!
    val googleBookId = bookInfo.data.id

    Card(
        modifier = Modifier.padding(34.dp),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = bookData.imageLinks.thumbnail),
            contentDescription = stringResource(id = R.string.book_image),
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .padding(1.dp)
        )
    }

    Text(
        text = bookData.title,
        style = MaterialTheme.typography.titleLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 19
    )

    Text(
        text = String.format(
            stringResource(id = R.string.author_param),
            bookData.authors.toString()
        )
    )

    Text(
        text = String.format(
            stringResource(id = R.string.page_count_param),
            bookData.pageCount.toString()
        )
    )

    Text(
        text = String.format(
            stringResource(id = R.string.categories_param),
            bookData.categories.toString()
        ),
        style = MaterialTheme.typography.titleSmall,
        overflow = TextOverflow.Ellipsis,
        maxLines = 3
    )

    Text(
        text = String.format(
            stringResource(id = R.string.published_param),
            bookData.publishedDate.toString()
        ),
        style = MaterialTheme.typography.titleSmall,
    )

    Spacer(modifier = Modifier.height(8.dp))

    val localDims = LocalContext.current.resources.displayMetrics

    Surface(
        modifier = Modifier
            .height(localDims.heightPixels.dp.times(0.1f))
            .padding(4.dp),
        shape = RectangleShape,
        border = BorderStroke(1.dp, Color.DarkGray)
    ) {
        val cleanDescription = HtmlCompat.fromHtml(
            bookData.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString()

        LazyColumn(modifier = Modifier.padding(4.dp)) {
            item {
                Text(text = cleanDescription)
            }
        }
    }

    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        RoundedButton(label = stringResource(id = R.string.save)) {
            val book = MBook(
                title = bookData.title,
                authors = bookData.authors.toString(),
                description = bookData.description,
                categories = bookData.categories.toString(),
                notes = "",
                photoUrl = bookData.imageLinks.thumbnail,
                publishedDate = bookData.publishedDate,
                pageCount = bookData.pageCount.toString(),
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            saveToFirebase(
                book = book,
                navController = navController
            )
        }

        Spacer(modifier = Modifier.width(25.dp))

        RoundedButton(label = stringResource(id = R.string.cancel)) {
            navController.popBackStack()
        }
    }
}

fun saveToFirebase(
    book: MBook,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection(Constants.BOOK_COLLECTION_FIREBASE_FIRESTORE)

    if (book.toString().isNotEmpty()) {
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(Constants.RESULT_UPDATED_LIST, true)

                            navController.popBackStack()
                        }
                    }.addOnFailureListener {
                        Log.w("Error Save Book", "saveToFirebase: Error updating doc", it)
                    }
            }
    }
}
