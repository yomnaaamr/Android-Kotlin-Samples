package com.example.nyttoday.ui.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nyttoday.R
import com.example.nyttoday.ui.theme.NYTTodayTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToSignUp: () -> Unit,
    navigateToMainScreen: () -> Unit
) {

    val uiState = viewModel.loginUiState
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var loading by rememberSaveable { mutableStateOf(false) }
    val isBottomSheetDismissed = rememberSaveable { mutableStateOf(false) }
    val successState by viewModel.uiState.collectAsStateWithLifecycle()


    if (successState is UiState.Success){
        LaunchedEffect(key1 = true) {
            navigateToMainScreen()
        }
    }


//    LaunchedEffect(key1 = successState) {
//        if(successState is UiState.Success){
//            navigateToMainScreen()
//        }
//    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
            { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { innerPadding ->





        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if(result.resultCode == Activity.RESULT_OK) {
                    scope.launch {
                        viewModel.signInWithIntent(
                            intent = result.data ?: return@launch,
                            context = context,
                            handleError = { errorMessage ->
                                scope.launch {
                                    if (errorMessage != null) {
                                        loading = false
                                        snackbarHostState.showSnackbar(errorMessage)
                                    }
                                }
                            }
                        )
                        loading = true
                    }
                }else {
                    isBottomSheetDismissed.value = true
                }
            }
        )


        LaunchedEffect(successState) {
            viewModel.uiState.collectLatest { state ->
                loading = when (state) {
                    is UiState.Loading -> true
                    else -> false
                }
            }
        }


        LaunchedEffect(isBottomSheetDismissed.value) {
            if (isBottomSheetDismissed.value) {
                // Perform the action when the bottom sheet is dismissed
                scope.launch {
                    loading = false
                    snackbarHostState.showSnackbar("Sign-in process was dismissed.")
                    isBottomSheetDismissed.value = false
                }
            }
        }



        LoginScreenContent(
            uiState = uiState,
            onValueChange = viewModel::updateUiState,
            onSignInClick = {
                viewModel.onSignInClick(
                    context = context,
                    handleError = { errorMessage ->
                        scope.launch {
                            if (errorMessage != null) {
                                snackbarHostState.showSnackbar(errorMessage)
                            }
                        }
                    }
                )
            },
            onForgotPasswordClick = {
                viewModel.onForgotPasswordClick(context) { errorMessage ->
                    scope.launch {
                        if (errorMessage != null) {
                            snackbarHostState.showSnackbar(errorMessage)
                        }

//                        viewModel.uiState.collect { state ->
//                            loading = when (state) {
//                                is UiState.Loading -> true
//                                else -> false
//                            }
//                        }

                    }
                }
            },
            navigateToSignUp = { navigateToSignUp() },
            modifier = Modifier
                .padding(innerPadding),
            loading = loading,
            onSignInWithGoogleClick = {
                scope.launch {
                    isBottomSheetDismissed.value = false
                    loading = true
                    val signInIntentSender = viewModel.signInWithGoogle(
                        context,
                        handleError = { errorMessage ->
                            scope.launch {
                                if (errorMessage != null) {
                                    snackbarHostState.showSnackbar(errorMessage)
                                }
                            }
                        }
                    )
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                    loading = false
                }
            }
        )
    }

}


@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onValueChange: (LoginUiState) -> Unit,
    onSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    navigateToSignUp: () -> Unit,
    loading: Boolean,
    onSignInWithGoogleClick: () -> Unit
) {


    Column(
        modifier = modifier
            .imePadding()
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
                .fillMaxWidth(),
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


        TextButton(
            onClick = { onForgotPasswordClick() },
            modifier = Modifier
                .align(Alignment.End),
            enabled = !loading
        ) {
            Text(
                text = stringResource(R.string.email_recovery),
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }


        Button(
            onClick = {
                onSignInClick()
            },
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            enabled = !loading
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = stringResource(R.string.sign_in), fontSize = 16.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge)

                if (loading) {

                    CircularProgressIndicator(
//                        progress = {
//                            0.8f
//                        },
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(30.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        trackColor = Color.White,
                        strokeCap = StrokeCap.Square,
                    )

                }

            }
        }



        TextButton(
            onClick = { navigateToSignUp() },
            modifier = Modifier
                .padding(top = 20.dp),
            enabled = !loading
        ) {
            Text(
                text = stringResource(R.string.new_account),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        }


        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(modifier = Modifier
                .height(0.5.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
            )

            Text(text = "OR",
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier
                .height(0.5.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
            )
        }


        OutlinedButton(onClick = {
            onSignInWithGoogleClick()
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            enabled = !loading
        ) {
            Text(
                text = stringResource(R.string.sign_in_with_google),
                Modifier.padding(horizontal = 8.dp),
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(painter = painterResource(id = R.drawable.icons8_google)
                , contentDescription = null,
                Modifier.size(24.dp),
                tint = if(!loading) Color.Unspecified else Color.Gray
            )
        }

    }


}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun LoginScreenPreview() {
    NYTTodayTheme {
        LoginScreenContent(
            uiState = LoginUiState(), onSignInClick = { /*TODO*/ },
            onValueChange = {},
            navigateToSignUp = {},
            onForgotPasswordClick = {},
            loading = true,
            onSignInWithGoogleClick = {}
        )


    }
}