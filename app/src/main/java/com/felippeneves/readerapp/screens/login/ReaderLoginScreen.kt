package com.felippeneves.readerapp.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.felippeneves.readerapp.R
import com.felippeneves.readerapp.components.ReaderLogo
import com.felippeneves.readerapp.components.input.EmailInput
import com.felippeneves.readerapp.components.input.PasswordInput
import com.felippeneves.readerapp.navigation.ReaderScreens

@Composable
fun ReaderLoginScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel = viewModel()
) {
    val showLoginForm = rememberSaveable { mutableStateOf(true) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ReaderLogo()
            if (showLoginForm.value) {
                UserForm(
                    loading = false,
                    isCreateAccount = false
                ) { email, password ->
                    viewModel.signInWithEmailAndPassword(email, password) {
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            } else {
                UserForm(
                    loading = false,
                    isCreateAccount = true
                ) { email, password ->
                    viewModel.createUserWithEmailAndPassword(email, password) {
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val text =
                if (showLoginForm.value) stringResource(id = R.string.sign_up) else stringResource(
                    id = R.string.login
                )
            Text(text = stringResource(id = R.string.new_user_question_mark))
            Text(
                text = text,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        showLoginForm.value = !showLoginForm.value
                    },
                fontWeight = FontWeight.Bold,
                color = Color(0xFF009688),
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, password -> }
) {
    //The data will never lost, even if the cell phone has been rotated
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    val modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        if (isCreateAccount) {
            Text(
                text = stringResource(id = R.string.rules_create_account),
                modifier = Modifier.padding(4.dp)
            )
        }

        EmailInput(
            emailState = email,
            enabled = !loading,
            onAction = KeyboardActions {
                passwordFocusRequest.requestFocus()
            }
        )

        PasswordInput(
            modifier = Modifier
                //When the previous field is filled, the current field gets focus
                .focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = stringResource(id = R.string.password),
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions

                onDone(email.value.trim(), password.value.trim())
            }
        )

        SubmitButton(
            textId = if (isCreateAccount) stringResource(id = R.string.create_account)
            else stringResource(id = R.string.login),
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    loading: Boolean,
    validInputs: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
        shape = CircleShape
    ) {
        if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else Text(text = textId, modifier = Modifier.padding(6.dp))
    }
}
