package com.felippeneves.readerapp.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.felippeneves.readerapp.components.ListCard
import com.felippeneves.readerapp.R
import com.felippeneves.readerapp.components.FABContent
import com.felippeneves.readerapp.components.Loading
import com.felippeneves.readerapp.components.ReaderAppBar
import com.felippeneves.readerapp.components.TitleSection
import com.felippeneves.readerapp.model.MBook
import com.felippeneves.readerapp.navigation.ReaderScreens
import com.felippeneves.readerapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderHomeScreen(
    navController: NavHostController,
    viewModel: ReaderHomeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ReaderAppBar(
                title = stringResource(id = R.string.a_reader),
                navController = navController,
                showProfile = true
            )
        },
        floatingActionButton = {
            FABContent(
                colorFAB = Color(0xFF92CBDF),
                icon = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_a_book),
                tint = Color.White
            ) {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HomeContent(navController, viewModel)
        }

    }
}

@Composable
fun HomeContent(
    navController: NavController,
    viewModel: ReaderHomeViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    //name@gmail.com
    val email = currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty()) email.split("@")[0] else "N/A"

    val listOfBooks = rememberSaveable(viewModel.data.value.data) {
        mutableStateOf(
            if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.toList().filter { mBook ->
                    mBook.userId == currentUser?.uid.toString()
                }
            } else {
                emptyList()
            })
    }

    val uploadedList = rememberSaveable {
        mutableStateOf(false)
    }

    //https://stackoverflow.com/questions/70609796/pass-data-to-previous-composable-in-android-compose
    //https://developersbreach.com/savedstatehandle-viewmodel-android/

    if (navController.currentBackStackEntry != null && navController.currentBackStackEntry!!.savedStateHandle.contains(Constants.RESULT_UPDATED_LIST)) {
        uploadedList.value =
            navController.currentBackStackEntry!!.savedStateHandle.get<Boolean>(
                Constants.RESULT_UPDATED_LIST
            ) ?: false
    }

    if (uploadedList.value) {
        viewModel.getAllBooksFromDatabase()
        navController.currentBackStackEntry!!.savedStateHandle.remove<Boolean>(Constants.RESULT_UPDATED_LIST)
        uploadedList.value = false
    }

    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = stringResource(id = R.string.your_reading_activity_right_now))

            Spacer(modifier = Modifier.fillMaxWidth(0.7f))

            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = stringResource(id = R.string.profile),
                    modifier = Modifier
                        .size(45.dp)
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        },
                    tint = Color(0, 105, 92)
                )

                Text(
                    text = currentUserName,
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )

                Divider()
            }
        }

        ReadingRightNowArea(
            listOfBooks = listOfBooks.value,
            navController = navController
        )

        TitleSection(label = stringResource(id = R.string.reading_list))

        BookListArea(
            listOfBooks = listOfBooks.value,
            navController = navController
        )
    }
}

@Composable
fun BookListArea(
    listOfBooks: List<MBook>,
    navController: NavController
) {
    //Book
    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(addedBooks) { googleBookId ->
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$googleBookId")
    }
}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<MBook>,
    viewModel: ReaderHomeViewModel = hiltViewModel(),
    onCardPressed: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {

        if (viewModel.data.value.loading == true) {
            Loading()
        } else {
            if (listOfBooks.isEmpty()) {
                Surface(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(id = R.string.no_books_found_add_a_book),
                        style = TextStyle(
                            color = Color.Red.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                for (book in listOfBooks) {
                    ListCard(book = book) { googleBookId ->
                        onCardPressed(googleBookId)
                    }
                }
            }
        }
    }
}

@Composable
fun ReadingRightNowArea(
    listOfBooks: List<MBook>,
    navController: NavController
) {
    //Filter books by reading now
    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(readingNowList) { googleBookId ->
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$googleBookId")
    }
}
