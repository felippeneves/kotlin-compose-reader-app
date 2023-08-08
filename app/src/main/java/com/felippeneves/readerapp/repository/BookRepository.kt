package com.felippeneves.readerapp.repository

import com.felippeneves.readerapp.data.Resource
import com.felippeneves.readerapp.model.Item
import com.felippeneves.readerapp.network.BooksApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BooksApi) {

    suspend fun getBooks(searchQuery: String): Resource<List<Item>> {
        return try {
            Resource.Loading(data = true)
            val itemList = api.getAllBooks(searchQuery).items
            if (itemList.isNotEmpty()) Resource.Loading(data = false)
            Resource.Success(data = itemList)
        } catch (e: Exception) {
            Resource.Error(message = e.message.toString())
        }
    }

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        val response = try {
            Resource.Loading(data = true)
            api.getBookInfo(bookId)
        } catch (e: Exception) {
            return Resource.Error(message = e.message.toString())
        }
        Resource.Loading(data = false)
        return Resource.Success(data = response)
    }

//    private val dataOrException = DataOrException<List<Item>, Boolean, Exception>()
//
//    private val bookInfoDataOrException = DataOrException<Item, Boolean, Exception>()
//
//    suspend fun getBooks(searchQuery: String): DataOrException<List<Item>, Boolean, Exception> {
//        try {
//            dataOrException.loading = true
//            dataOrException.data = api.getAllBooks(searchQuery).items
//            if (dataOrException.data!!.isNotEmpty()) dataOrException.loading = false
//        } catch (e: Exception) {
//            dataOrException.e = e
//        }
//
//        return dataOrException
//    }
//
//    suspend fun getBookInfo(bookId: String): DataOrException<Item, Boolean, Exception> {
//       try {
//            bookInfoDataOrException.loading = true
//            bookInfoDataOrException.data = api.getBookInfo(bookId)
//            if (bookInfoDataOrException.data.toString().isNotEmpty()) bookInfoDataOrException.loading = false
//        } catch (e: Exception) {
//            bookInfoDataOrException.e = e
//        }
//
//        return bookInfoDataOrException
//    }
}