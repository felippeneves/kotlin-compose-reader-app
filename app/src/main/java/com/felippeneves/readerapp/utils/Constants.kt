package com.felippeneves.readerapp.utils

object Constants {
    //https://www.googleapis.com/books/v1/volumes?q=flutter
    const val BASE_URL = "https://www.googleapis.com/books/v1/"
    const val VOLUMES_API = "volumes"
    const val FILTER_QUERY_KIND_OF_BOOK = "q"
    const val BOOK_ID_PARAMETER = "{bookId}"
    const val SMALL_THUMBNAIL_DEFAULT = "http://books.google.com/books/content?id=5BGBswAQSiEC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
    const val BOOK_QUERY_DEFAULT = "android"
    const val BOOK_ID_ARGUMENT = "bookId"
    const val BOOK_COLLECTION_FIREBASE_FIRESTORE = "books"
    const val BOOK_ITEM_ID_ARGUMENT = "bookItemId"

    const val RESULT_UPDATED_LIST = "updatedList"
}