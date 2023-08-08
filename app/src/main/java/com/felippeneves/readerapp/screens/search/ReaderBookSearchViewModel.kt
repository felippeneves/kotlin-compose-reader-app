package com.felippeneves.readerapp.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felippeneves.readerapp.data.Resource
import com.felippeneves.readerapp.model.Item
import com.felippeneves.readerapp.repository.BookRepository
import com.felippeneves.readerapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderBookSearchViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks(Constants.BOOK_QUERY_DEFAULT)
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty())
                return@launch

            try {
                when (val response = repository.getBooks(query)) {
                    is Resource.Success -> {
                        list = response.data!!
                        if (list.isNotEmpty()) isLoading = false
                    }

                    is Resource.Error -> {
                        isLoading = false
                        Log.e("Network", "searchBooks: Failed getting books")
                    }

                    else -> {
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                isLoading = false
                Log.d("Network", "searchBooks: ${e.message.toString()}")
            }
        }
    }


//    val listOfBooks: MutableState<DataOrException<List<Item>, Boolean, Exception>> =
//        mutableStateOf(DataOrException(null, false, Exception("")))
//
//    init {
//        searchBooks("android")
//    }
//
//    fun searchBooks(query: String) {
//        viewModelScope.launch {
//            if (query.isEmpty())
//                return@launch
//
//            listOfBooks.value.loading = true
//            listOfBooks.value = repository.getBooks(query)
//            Log.d("DATA BOOKS", "searchBooks: ${listOfBooks.value.data.toString()}")
//            if (listOfBooks.value.toString().isNotEmpty()) listOfBooks.value.loading = false
//        }
//    }
}