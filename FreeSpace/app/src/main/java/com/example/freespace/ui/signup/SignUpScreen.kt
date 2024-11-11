package com.example.freespace.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.freespace.R
import com.example.freespace.ui.login.UiState
import com.example.freespace.ui.navigation.NavigationDestination
import com.example.freespace.ui.theme.FreeSpaceTheme
import kotlinx.coroutines.launch


object SignUpDestination : NavigationDestination {
    override val route = "signup"
}

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel(),
    alreadyHaveAccount: () -> Unit
) {

    val uiState = viewModel.signupUiState
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var loading by rememberSaveable { mutableStateOf(false) }



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
            { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { innerPadding ->


        LaunchedEffect(Unit) {
            viewModel.signUpUiState.collect { state ->
                loading = when (state) {
                    is UiState.Loading -> true
                    else -> false
                }
            }
        }

        SignUpContent(
            uiState = uiState,
            onValueChange = viewModel::updateUiState,
            onSignUpClick = {
                viewModel.onSignUpClick(
                    navController,
                    context,
                    handleError = {errorMessage ->
                        scope.launch {
                            if (errorMessage != null) {
                                snackBarHostState.showSnackbar(errorMessage)
                            }
                        }
                    }
                )


//                { errorMessage ->
//                    scope.launch {
//                        if (errorMessage != null) {
//                            snackBarHostState.showSnackbar(errorMessage)
//                        }
//
//                        viewModel.signUpUiState.collect { state ->
//                            loading = when (state) {
//                                is UiState.Loading -> true
//                                else -> false
//                            }
//                        }
//
//                    }
//
//                }
            },
            alreadyHaveAccount = { alreadyHaveAccount() },
            modifier = Modifier
                .padding(innerPadding),
            loading = loading
        )
    }

}

@Composable
fun SignUpContent(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    onValueChange: (SignUpUiState) -> Unit,
    onSignUpClick: () -> Unit,
    alreadyHaveAccount: () -> Unit,
    loading: Boolean
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            value = uiState.name,
            onValueChange = {
                onValueChange(uiState.copy(name = it))
            },
            placeholder = {
                Text(text = stringResource(R.string.name))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Email"
                )
            },
            enabled = !loading,
            shape = MaterialTheme.shapes.extraLarge
        )



        OutlinedTextField(
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            value = uiState.email,
            onValueChange = {
                onValueChange(uiState.copy(email = it))
            },
            placeholder = {
                Text(text = stringResource(R.string.email))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            },
            enabled = !loading,
            shape = MaterialTheme.shapes.extraLarge
        )


        var isVisible by remember { mutableStateOf(false) }

        val visibleIcon = if (isVisible) rememberVectorPainter(image = Icons.Default.Visibility)
        else rememberVectorPainter(image = Icons.Default.VisibilityOff)

        val visualTransformation =
            if (isVisible) VisualTransformation.None else PasswordVisualTransformation()


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            value = uiState.password,
            onValueChange = {
                onValueChange(uiState.copy(password = it))
            },
            placeholder = { Text(text = stringResource(R.string.password)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
            trailingIcon = {
                IconButton(onClick = { isVisible = !isVisible }) {
                    Icon(painter = visibleIcon, contentDescription = "Visibility")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = visualTransformation,
            enabled = !loading,
            shape = MaterialTheme.shapes.extraLarge
        )


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            value = uiState.repeatPassword,
            onValueChange = {
                onValueChange(uiState.copy(repeatPassword = it))
            },
            placeholder = { Text(text = stringResource(R.string.repeat_pass)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
            trailingIcon = {
                IconButton(onClick = { isVisible = !isVisible }) {
                    Icon(painter = visibleIcon, contentDescription = "Visibility")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = visualTransformation,
            enabled = !loading,
            shape = MaterialTheme.shapes.extraLarge
        )



        Button(
            onClick = { onSignUpClick() },
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(bottom = 20.dp),
            shape = MaterialTheme.shapes.extraLarge,
            enabled = !loading
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = stringResource(R.string.sign_up), fontSize = 16.sp)


                if (loading) {

                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
//                    trackColor = Color.White,
                        strokeCap = StrokeCap.Square,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(30.dp),
                        progress = 0.8f

                    )

                }

            }
        }


        TextButton(
            onClick = { alreadyHaveAccount() },
            modifier = Modifier
                .padding(top = 20.dp),
            enabled = !loading
        ) {
            Text(text = stringResource(R.string.already_has_account))
        }


    }

}


@Preview
@Composable
private fun SignUpPreview() {
    FreeSpaceTheme {
        SignUpContent(
            uiState = SignUpUiState(),
            onValueChange = {},
            onSignUpClick = {},
            alreadyHaveAccount = {},
            loading = false
        )
    }
}