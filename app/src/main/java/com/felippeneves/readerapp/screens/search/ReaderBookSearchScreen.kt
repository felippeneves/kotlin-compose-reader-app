package com.felippeneves.readerapp.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.felippeneves.readerapp.R
import com.felippeneves.readerapp.components.Loading
import com.felippeneves.readerapp.components.ReaderAppBar
import com.felippeneves.readerapp.components.input.InputField
import com.felippeneves.readerapp.model.Item
import com.felippeneves.readerapp.navigation.ReaderScreens
import com.felippeneves.readerapp.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderBookSearchScreen(
    navController: NavController,
    viewModel: ReaderBookSearchViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = stringResource(id = R.string.search_books),
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
            Column {
                SearchForm { searchQuery ->
                    viewModel.searchBooks(query = searchQuery)
                }

                Spacer(modifier = Modifier.height(16.dp))
                BookList(navController = navController, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    onSearch: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val searchQueryState = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }


        InputField(
            valueState = searchQueryState,
            labelId = stringResource(id = R.string.search),
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions

                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            }
        )
    }
}

@Composable
fun BookList(
    navController: NavController,
    viewModel: ReaderBookSearchViewModel = hiltViewModel()
) {
    val listOfBooks = viewModel.list

    if (viewModel.isLoading) {
        Loading()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items = listOfBooks) { book ->
                BookRow(book = book, navController = navController)
            }
        }
    }
}

@Composable
fun BookRow(
    book: Item,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(4.dp)
            .clickable {
                navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
            },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl: String =
                book.volumeInfo.imageLinks.smallThumbnail.ifEmpty { Constants.SMALL_THUMBNAIL_DEFAULT }

            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = stringResource(id = R.string.book_image),
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )

            Column {
                Text(
                    text = book.volumeInfo.title,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = String.format(
                        stringResource(id = R.string.author_param),
                        book.volumeInfo.authors
                    ),
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = String.format(
                        stringResource(id = R.string.author_param),
                        book.volumeInfo.authors
                    ),
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = String.format(
                        stringResource(id = R.string.date_param),
                        book.volumeInfo.publishedDate
                    ),
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${book.volumeInfo.categories}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
