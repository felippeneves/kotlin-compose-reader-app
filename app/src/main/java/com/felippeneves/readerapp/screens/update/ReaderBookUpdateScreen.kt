package com.felippeneves.readerapp.screens.update

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.felippeneves.readerapp.R
import com.felippeneves.readerapp.components.Loading
import com.felippeneves.readerapp.components.RatingBar
import com.felippeneves.readerapp.components.ReaderAppBar
import com.felippeneves.readerapp.components.RoundedButton
import com.felippeneves.readerapp.components.ShowAlertDialog
import com.felippeneves.readerapp.components.input.InputField
import com.felippeneves.readerapp.data.DataOrException
import com.felippeneves.readerapp.model.MBook
import com.felippeneves.readerapp.screens.home.ReaderHomeViewModel
import com.felippeneves.readerapp.utils.Constants
import com.felippeneves.readerapp.utils.formatDate
import com.felippeneves.readerapp.utils.showToast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderBookUpdateScreen(
    navController: NavHostController,
    bookItemId: String,
    viewModel: ReaderHomeViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = stringResource(id = R.string.update_book),
            icon = Icons.Default.ArrowBack,
            navController = navController,
        ) {
            navController.popBackStack()
        }
    }) { paddingValues ->

        val bookInfo = produceState<DataOrException<List<MBook>, Boolean, Exception>>(
            initialValue =
            DataOrException(data = emptyList(), true, Exception(""))
        ) {
            value = viewModel.data.value
        }.value

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bookInfo.loading == true) {
                    Loading()
                    bookInfo.loading = false
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        shadowElevation = 4.dp
                    ) {
                        ShowBookUpdate(
                            bookInfo = viewModel.data.value,
                            bookItemId = bookItemId
                        )
                    }

                    ShowSimpleForm(
                        book = viewModel.data.value.data?.first { mBook ->
                            mBook.googleBookId == bookItemId
                        }!!,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun ShowSimpleForm(
    book: MBook,
    navController: NavHostController
) {
    val context = LocalContext.current

    val notesText = remember {
        mutableStateOf("")
    }
    val isStartedReading = remember {
        mutableStateOf(false)
    }

    val isFinishedReading = remember {
        mutableStateOf(false)
    }

    val ratingVal = remember {
        mutableStateOf(0)
    }

    SimpleForm(
        defaultValue = book.notes.toString().ifEmpty { stringResource(id = R.string.no_thoughts_available) }
    ) { note ->
        notesText.value = note
    }

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(
            onClick = { isStartedReading.value = true },
            enabled = book.startedReading == null
        ) {
            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = stringResource(id = R.string.start_reading))
                } else {
                    Text(
                        text = stringResource(id = R.string.started_reading),
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(
                    text = String.format(
                        stringResource(id = R.string.started_on_param),
                        formatDate(book.startedReading!!)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = { isFinishedReading.value = true },
            enabled = book.finishedReading == null
        ) {
            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = stringResource(id = R.string.mark_as_read))
                } else {
                    Text(
                        text = stringResource(id = R.string.finished_reading),
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(
                    text = String.format(
                        stringResource(id = R.string.finished_on_param),
                        formatDate(book.finishedReading!!)
                    )
                )
            }
        }
    }

    Text(
        text = stringResource(id = R.string.rating),
        modifier = Modifier.padding(bottom = 3.dp)
    )

    book.rating?.toInt().let {
        RatingBar(rating = it!!) { rating ->
            ratingVal.value = rating
        }
    }

    Spacer(modifier = Modifier.padding(bottom = 16.dp))

    Row {
        val changedNotes = book.notes != notesText.value
        val changedRating = book.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp =
            if (isFinishedReading.value) Timestamp.now() else book.finishedReading
        val isStartedTimeStamp =
            if (isStartedReading.value) Timestamp.now() else book.startedReading

        val bookUpdate =
            changedNotes || changedRating || isFinishedReading.value || isFinishedReading.value

        val bookTopUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to notesText.value
        ).toMap()

        RoundedButton(label = stringResource(id = R.string.update)) {
            if (bookUpdate) {
                FirebaseFirestore.getInstance()
                    .collection(Constants.BOOK_COLLECTION_FIREBASE_FIRESTORE)
                    .document(book.id!!)
                    .update(bookTopUpdate)
                    .addOnCompleteListener {
                        showToast(
                            context = context,
                            msg = context.resources.getString(R.string.book_updated_successfully)
                        )
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(Constants.RESULT_UPDATED_LIST, true)

                        navController.popBackStack()
                    }.addOnFailureListener { exception ->
                        Log.w("Error Update", "ShowSimpleForm: Error updating document", exception)
                    }
            }
        }

        Spacer(modifier = Modifier.width(100.dp))

        val openDialog = remember {
            mutableStateOf(false)
        }

        if (openDialog.value) {
            ShowAlertDialog(
                title = stringResource(id = R.string.delete_book),
                message = stringResource(id = R.string.delete_book_confirmation_message),
                confirmButtonTitle = stringResource(id = R.string.yes),
                dismissButtonTitle = stringResource(id = R.string.no),
                openDialog = openDialog
            ) {
                FirebaseFirestore.getInstance()
                    .collection(Constants.BOOK_COLLECTION_FIREBASE_FIRESTORE)
                    .document(book.id!!)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            openDialog.value = false

                            navController.popBackStack()
                        }
                    }
            }
        }

        RoundedButton(label = stringResource(id = R.string.delete)) {
            openDialog.value = true
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = stringResource(id = R.string.great_book),
    onSearch: (String) -> Unit
) {
    Column() {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) {
            textFieldValue.value.trim().isNotEmpty()
        }

        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(4.dp)
                .background(
                    Color.White,
                    CircleShape
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
            valueState = textFieldValue,
            labelId = stringResource(id = R.string.enter_your_thoughts),
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            }
        )
    }
}

@Composable
fun ShowBookUpdate(
    bookInfo: DataOrException<List<MBook>, Boolean, Exception>,
    bookItemId: String
) {
    Row {
        Spacer(modifier = Modifier.width(44.dp))
        if (bookInfo.data != null) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CardListItem(book = bookInfo.data!!.first() { mBook ->
                    mBook.googleBookId == bookItemId
                }, onPressDetails = {})
            }
        }
    }
}

@Composable
fun CardListItem(
    book: MBook,
    onPressDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(
                start = 4.dp,
                end = 4.dp,
                top = 4.dp,
                bottom = 8.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { },
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberImagePainter(data = book.photoUrl.toString()),
                contentDescription = stringResource(id = R.string.book_image),
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp,
                            topEnd = 20.dp
                        )
                    )
            )

            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp
                        )
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 2.dp
                        )
                        .width(120.dp),
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .width(120.dp),
                )
            }
        }
    }
}
