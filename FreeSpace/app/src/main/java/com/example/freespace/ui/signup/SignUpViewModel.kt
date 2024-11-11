package com.example.freespace.ui.signup

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.freespace.R
import com.example.freespace.firebase.service.AccountService
import com.example.freespace.isValidEmail
import com.example.freespace.isValidPassword
import com.example.freespace.passwordMatches
import com.example.freespace.ui.home.HomeDestination
import com.example.freespace.ui.login.UiState
import com.example.freespace.ui.login.isNetworkAvailable
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    var signupUiState by mutableStateOf(SignUpUiState())
        private set


    private val _signUpUiState = MutableStateFlow<UiState>(UiState.Idle)
    val signUpUiState = _signUpUiState.asStateFlow()

    private val email
        get() = signupUiState.email

    private val password
        get() = signupUiState.password

    private val repeatPassword
        get() = signupUiState.repeatPassword
    private val name
        get() = signupUiState.name



    fun updateUiState(signUpUiState: SignUpUiState) {
        signupUiState = SignUpUiState(
            name = signUpUiState.name,
            email = signUpUiState.email,
            password = signUpUiState.password,
            repeatPassword = signUpUiState.repeatPassword
        )
    }

    fun onSignUpClick(
        navController: NavController, context: Context,
        handleError: (String?) -> Unit
    ) {

        val errorMessage = validateCredentials(name, email, password, repeatPassword, context)

        if (errorMessage.isNotBlank()) {
            _signUpUiState.value = UiState.Error
            handleError(errorMessage)
            return
        }


        if (!isNetworkAvailable(context)) {
            _signUpUiState.value = UiState.Error
            handleError("لا يوجد اتصال بالانترنت ؛ من فضلك قم بالاتصال وحاول مرة اخري")
            return
        }


        _signUpUiState.value = UiState.Loading

        viewModelScope.launch {

            try {

                withContext(Dispatchers.IO) {
                    accountService.signUp(email, password,name)
                }
                _signUpUiState.value = UiState.Success
                navController.navigate(HomeDestination.route){
//                    popUpTo(route = LoginDestination.route){
//                        inclusive = true
//                    }
                    popUpTo(navController.graph.id){
                        inclusive = true
                    }
                }


            } catch (error: Throwable) {

                val message: String = when (error) {
                    is FirebaseAuthUserCollisionException -> " البريد الإلكتروني المستخدم موجود بالفعل ، حاول تسجيل الدخول."
                    is SocketTimeoutException, is UnknownHostException,
                    is FirebaseAuthException-> "حدث خطأ ما أثناء عملية التسجيل ؛ من فضلك تأكد من اتصالك بالانترنت وحاول مرة اخري"
                    else -> "حدث خطأ ما؛ من فضلك حاول مرة اخري"


                }
                _signUpUiState.value = UiState.Error
                handleError(message)
            }

        }

    }


    private fun validateCredentials(
        name: String,
        email: String,
        password: String,
        repeatPassword: String,
        context: Context
    ): String {

        val errors = mutableListOf<String>()

        if (name.isBlank()) {
            errors.add(context.getString(R.string.valid_name))
        }

        if (!email.isValidEmail()) {
            errors.add(context.getString(R.string.valid_email))
        }

        if (!password.isValidPassword()) {
            errors.add(context.getString(R.string.valid_pass))
        }

        if (!password.passwordMatches(repeatPassword)) {
            errors.add(context.getString(R.string.pass_match))
        }

        return errors.joinToString("\n")

    }


}


data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = ""
)