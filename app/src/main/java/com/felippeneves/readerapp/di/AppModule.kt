package com.felippeneves.readerapp.di

import com.felippeneves.readerapp.network.BooksApi
import com.felippeneves.readerapp.repository.BookRepository
import com.felippeneves.readerapp.repository.FireRepository
import com.felippeneves.readerapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideBookApi(): BooksApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

    @Singleton
    @Provides
    fun provideBookRepository(api: BooksApi) = BookRepository(api)

    @Singleton
    @Provides
    fun provideFireBookRepository() = FireRepository(
        queryBook = FirebaseFirestore
            .getInstance()
            .collection(Constants.BOOK_COLLECTION_FIREBASE_FIRESTORE)
    )
}