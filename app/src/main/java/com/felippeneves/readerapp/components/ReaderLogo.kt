package com.felippeneves.readerapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felippeneves.readerapp.R

@Composable
fun ReaderLogo(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.a_reader),
        modifier = modifier.padding(bottom = 16.dp),
        style = MaterialTheme.typography.displayMedium,
        color = Color.Red.copy(alpha = 0.5f)
    )
}