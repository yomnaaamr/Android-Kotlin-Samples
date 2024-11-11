package com.example.freespace.ui.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.freespace.R
import com.example.freespace.firebase.service.AccountService
import com.example.freespace.isValidEmail
import com.example.freespace.ui.home.HomeDestination
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
) : ViewModel() {

    var loginUiState by mutableStateOf(LoginUiState())
        private set

//    private val _snackbarMessage = MutableStateFlow<String?>(null)
//    val snackbarMessage= _snackbarMessage.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()


    private val email
        get() = loginUiState.email
    private val password
        get() = loginUiState.password

    fun updateUiState(uiState: LoginUiState) {

        loginUiState =
            LoginUiState(email = uiState.email, password = uiState.password)

    }


    suspend fun signInWithIntent(intent: Intent,context: Context,
                                 handleError: (String?) -> Unit,
                                 navController: NavController
    ){
        if (!isNetworkAvailable(context)) {
            _uiState.value = UiState.Error
            handleError("لا يوجد اتصال بالانترنت ؛ من فضلك قم بالاتصال وحاول مرة اخري")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO){
                    accountService.signInWithIntent(intent)
                }

                withContext(Dispatchers.Main){
                    _uiState.value = UiState.Success
                    navController.navigate(HomeDestination.route){
//                        popUpTo(route = LoginDestination.route){
//                            inclusive = true
//                        }
                        popUpTo(navController.graph.id){
                            inclusive = true
                        }
                    }
                }
            }catch (error: Throwable){
                _uiState.value = UiState.Error
                handleError(error.message)
            }
        }
    }


    suspend fun signInWithGoogle(context: Context,
                                 handleError: (String?) -> Unit
    ): IntentSender?{

        _uiState.value = UiState.Loading

        if (isNetworkAvailable(context)){
            return accountService.signInWithGoogle()
        }else{
            _uiState.value = UiState.Error
            handleError("لا يوجد اتصال بالانترنت ؛ من فضلك قم بالاتصال وحاول مرة اخري")
            return null
        }
    }


    fun onSignInClick(
        navController: NavController, context: Context,
        handleError: (String?) -> Unit
    ) {
        val errorMessage = validateCredentials(email, password, context)

        if (errorMessage.isNotBlank()) {
            Log.d("LoginViewModel", "Validation error: $errorMessage")
            _uiState.value = UiState.Error
            handleError(errorMessage)
            return
        }

        if (!isNetworkAvailable(context)) {
            Log.d("LoginViewModel", "No network available")
            _uiState.value = UiState.Error
            handleError("لا يوجد اتصال بالانترنت ؛ من فضلك قم بالاتصال وحاول مرة اخرى")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    accountService.authenticate(email, password)
                }
                Log.d("LoginViewModel", "Sign in successful")
                _uiState.value = UiState.Success
                navController.navigate(HomeDestination.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            } catch (error: Throwable) {
                val message: String = when (error) {
                    is FirebaseAuthInvalidCredentialsException -> "البيانات المدخلة غير صحيحة، تأكد من البريد الإلكتروني وكلمة المرور."
                    is FirebaseAuthUserCollisionException -> {
                        if (error.message?.contains("google.com") == true) {
                            "هذا البريد الإلكتروني مستخدم بحساب Google. برجاء تسجيل الدخول باستخدام Google."
                        } else {
                            "هذا البريد الإلكتروني مستخدم بالفعل بحساب آخر."
                        }
                    }
                    is FirebaseAuthException -> "خطأ في البريد الإلكتروني أو كلمة المرور ؛ من فضلك حاول مرة اخرى."
                    is SocketTimeoutException, is UnknownHostException -> "حدث خطأ ما أثناء عملية التسجيل ؛ من فضلك تأكد من اتصالك بالانترنت وحاول مرة اخرى."
                    else -> "حدث خطأ ما؛ من فضلك حاول مرة اخرى."
                }
                Log.e("LoginViewModel", "Sign in error: ${error.message}", error)
                _uiState.value = UiState.Error
                handleError(message)
            }
        }
    }


//    fun onSignInClick(
//        navController: NavController, context: Context,
//        handleError: (String?) -> Unit
//    ) {
//
//        val errorMessage = validateCredentials(email, password, context)
//
//        if (errorMessage.isNotBlank()) {
//            _uiState.value = UiState.Error
//            handleError(errorMessage)
//            return
//        }
//
//        if (!isNetworkAvailable(context)) {
//            _uiState.value = UiState.Error
//            handleError("لا يوجد اتصال بالانترنت ؛ من فضلك قم بالاتصال وحاول مرة اخري")
//            return
//        }
//
//        _uiState.value = UiState.Loading
//
//        viewModelScope.launch {
//
//            try {
//
//                withContext(Dispatchers.IO) {
//                    accountService.authenticate(email, password)
//                }
//                _uiState.value = UiState.Success
//                navController.navigate(HomeDestination.route){
////                    popUpTo(route = LoginDestination.route){
////                        inclusive = true
////                    }
//                    popUpTo(navController.graph.id){
//                        inclusive = true
//                    }
//                }
//
//            }catch (error: Throwable) {
//
//                val message: String = when (error) {
//                    is FirebaseAuthException -> "خطأ في البريد الالكتروني أو كلمة المرور ؛من فضلك حاول مرة اخري"
//                    is SocketTimeoutException, is UnknownHostException -> "حدث خطأ ما أثناء عملية التسجيل ؛ من فضلك تأكد من اتصالك بالانترنت وحاول مرة اخري"
//                    else -> "حدث خطأ ما؛ من فضلك حاول مرة اخري"
//
//                }
//                _uiState.value = UiState.Error
//                handleError(message)
//
//            }
//        }
//
//
//    }

    private fun validateCredentials(email: String, password: String, context: Context): String {
        val errors = mutableListOf<String>()
        if (!email.isValidEmail()) {
            errors.add(context.getString(R.string.valid_email))
        }
        if (password.isBlank()) {
            errors.add(context.getString(R.string.valid_password))
        }
        return errors.joinToString("\n")
    }


    fun onForgotPasswordClick(context: Context,
                                handleError: (String?) -> Unit) {

        if (!email.isValidEmail()) {
            _uiState.value = UiState.Error
            handleError(context.getString(R.string.valid_email))
            return
        }

        if (!isNetworkAvailable(context)) {
            _uiState.value = UiState.Error
            handleError("لا يوجد اتصال بالانترنت ؛ من فضلك قم بالاتصال وحاول مرة اخري")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch {

            try {
                withContext(Dispatchers.IO){
                    accountService.sendRecoveryEmail(email)
                }
                _uiState.value = UiState.Success
                handleError(context.getString(R.string.inbox))
            }catch (e: Throwable){

                val message: String = when (e) {
                    is FirebaseAuthException -> "خطأ في البريد الالكتروني ؛من فضلك حاول مرة اخري"
                    else -> "حدث خطأ ما؛ من فضلك حاول مرة اخري"

                }
                _uiState.value = UiState.Error
                handleError(message)
            }
        }
    }
}


fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return networkCapabilities != null && (
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            )
}


data class LoginUiState(
    val email: String = "",
    val password: String = ""
)


sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data object Error : UiState()
}