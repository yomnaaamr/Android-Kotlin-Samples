package com.example.nyttoday.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyttoday.firebase.service.AccountService
import com.example.nyttoday.firebase.service.StorageService
import com.example.nyttoday.ui.login.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : ViewModel() {

    private val _uiStateAccount = MutableStateFlow<UiState>(UiState.Idle)
    val uiStateAccount = _uiStateAccount.asStateFlow()

    fun signOut() {

        _uiStateAccount.value = UiState.Loading

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accountService.signOut()
            }
            _uiStateAccount.value = UiState.Success
        }
    }


    fun deleteAccount() {
        _uiStateAccount.value = UiState.Loading

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storageService.deleteAllUserData()
                accountService.deleteAccount()
            }
            withContext(Dispatchers.Main){
                _uiStateAccount.value = UiState.Success
            }

        }
    }
}