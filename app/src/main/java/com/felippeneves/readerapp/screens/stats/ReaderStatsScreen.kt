package com.felippeneves.readerapp.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.felippeneves.readerapp.R
import com.felippeneves.readerapp.components.Loading
import com.felippeneves.readerapp.components.ReaderAppBar
import com.felippeneves.readerapp.model.MBook
import com.felippeneves.readerapp.screens.home.ReaderHomeViewModel
import com.felippeneves.readerapp.utils.Constants
import com.felippeneves.readerapp.utils.formatDate
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderStatsScreen(
    navController: NavHostController,
    viewModel: ReaderHomeViewModel
) {
    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        ReaderAppBar(
            title = stringResource(id = R.string.book_stats),
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
            //Only show books by this user that have been read
            books = if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            } else {
                emptyList()
            }

            Column {
                Row {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.Person,
                            contentDescription = stringResource(id = R.string.person_icon)
                        )
                    }

                    Text(
                        text = String.format(
                            stringResource(id = R.string.hi_param),
                            currentUser?.email.toString().split("@")[0].uppercase(Locale.ROOT)
                        )
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    val readBookList: List<MBook> =
                        if (!viewModel.data.value.data.isNullOrEmpty()) {
                            books.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            }
                        } else {
                            emptyList()
                        }

                    val readingBooks = books.filter { mBook ->
                        (mBook.startedReading != null) && mBook.finishedReading == null
                    }

                    Column(
                        modifier = Modifier.padding(
                            start = 25.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = stringResource(id = R.string.your_stats),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Divider()
                        Text(
                            text = String.format(
                                stringResource(id = R.string.you_are_reading_books_param),
                                readingBooks.size
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = String.format(
                                stringResource(id = R.string.you_have_read_books_param),
                                readBookList.size
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                if (viewModel.data.value.loading == true) {
                    Loading()
                } else {
                    Divider()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        //Filter books by finished ones
                        val readBooks: List<MBook> =
                            if (!viewModel.data.value.data.isNullOrEmpty()) {
                                viewModel.data.value.data!!.filter { mBook ->
                                    (mBook.userId == currentUser?.uid) && mBook.finishedReading != null
                                }
                            } else {
                                emptyList()
                            }
                        items(items = readBooks) { book ->
                            BookRow(book = book)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookRow(book: MBook) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl: String =
                book.photoUrl.toString().ifEmpty { Constants.SMALL_THUMBNAIL_DEFAULT }

            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = stringResource(id = R.string.book_image),
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        text = book.title.toString(),
                        overflow = TextOverflow.Ellipsis,
                    )

                    if (book.rating!! >= 4) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = stringResource(id = R.string.thumbs_up_icon),
                            tint = Color.Green.copy(alpha = 0.5f)
                        )
                    }
                }

                Text(
                    text = String.format(
                        stringResource(id = R.string.author_param),
                        book.authors
                    ),
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = String.format(
                        stringResource(id = R.string.started_param),
                        formatDate(book.startedReading!!)
                    ),
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = String.format(
                        stringResource(id = R.string.finished_param),
                        formatDate(book.finishedReading!!)
                    ),
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}