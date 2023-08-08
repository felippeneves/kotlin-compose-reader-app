package com.felippeneves.readerapp.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

@Composable
fun ShowAlertDialog(
    title: String,
    message: String,
    confirmButtonTitle: String,
    dismissButtonTitle: String,
    openDialog: MutableState<Boolean>,
    onConfirmPressed: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(onClick = { onConfirmPressed.invoke() }) {
                    Text(text = confirmButtonTitle)
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text(text = dismissButtonTitle)
                }
            },
            containerColor = Color.White,
            onDismissRequest = { openDialog.value = false }
        )
    }
}