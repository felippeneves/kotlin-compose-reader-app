package com.felippeneves.readerapp.utils

import android.content.Context
import android.icu.text.DateFormat
import android.widget.Toast
import com.google.firebase.Timestamp

fun formatDate(timestamp: Timestamp): String {
    return DateFormat.getDateInstance()
        .format(timestamp.toDate())
        .toString().split(",")[0] //August 5, 2023
}

fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}