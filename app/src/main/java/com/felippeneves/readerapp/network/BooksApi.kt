package com.felippeneves.readerapp.network

import com.felippeneves.readerapp.model.Book
import com.felippeneves.readerapp.model.Item
import com.felippeneves.readerapp.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface BooksApi {
    @GET(Constants.VOLUMES_API)
    suspend fun getAllBooks(@Query(Constants.FILTER_QUERY_KIND_OF_BOOK) query: String): Book

    @GET(Constants.VOLUMES_API + "/" + Constants.BOOK_ID_PARAMETER)
    suspend fun getBookInfo(@Path(Constants.BOOK_ID_ARGUMENT) bookId: String): Item
}