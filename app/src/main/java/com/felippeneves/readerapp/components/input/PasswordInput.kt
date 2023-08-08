package com.felippeneves.readerapp.components.input

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelId: String,
    enabled: Boolean = true,
    isSingleLine: Boolean = true,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onAction: KeyboardActions
) {
    val visualTransformation =
        if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation()

    InputField(
        modifier = modifier,
        valueState = passwordState,
        labelId = labelId,
        enabled = enabled,
        isSingleLine = isSingleLine,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        onAction = onAction,
        visualTransformation = visualTransformation,
        trailingIcon = {
            PasswordVisibility(passwordVisibility = passwordVisibility)
        }
    )
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = {
        passwordVisibility.value = !visible
    }) {
        Icons.Default.Close
    }
}