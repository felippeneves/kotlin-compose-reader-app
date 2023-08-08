package com.felippeneves.readerapp.screens.home


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felippeneves.readerapp.data.DataOrException
import com.felippeneves.readerapp.model.MBook
import com.felippeneves.readerapp.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderHomeViewModel @Inject constructor(
    private val repository: FireRepository
) : ViewModel() {
    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>> =
        mutableStateOf(DataOrException(listOf(), true, Exception("")))

    init {
        getAllBooksFromDatabase()
    }

    fun getAllBooksFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromDatabase()
            if (!data.value.data.isNullOrEmpty()) data.value.loading = false
        }
    }
}