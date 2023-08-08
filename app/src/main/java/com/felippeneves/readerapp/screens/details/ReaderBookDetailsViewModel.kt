package com.felippeneves.readerapp.screens.details

import androidx.lifecycle.ViewModel
import com.felippeneves.readerapp.data.Resource
import com.felippeneves.readerapp.model.Item
import com.felippeneves.readerapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReaderBookDetailsViewModel @Inject constructor(
    private val repository: BookRepository
): ViewModel() {
    suspend fun getBookInfo(bookId: String): Resource<Item> {
        return repository.getBookInfo(bookId)
    }
}