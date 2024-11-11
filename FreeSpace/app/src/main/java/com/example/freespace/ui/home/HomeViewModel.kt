package com.example.freespace.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.freespace.data.Note
import com.example.freespace.data.NotesRepository
import com.example.freespace.firebase.service.AccountService
import com.example.freespace.firebase.service.StorageService
import com.example.freespace.ui.login.LoginDestination
import com.example.freespace.ui.login.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val accountService: AccountService,
    private val storageService: StorageService,
//    private val context: Context,  makes memory leak
    application: Application
) : ViewModel()
{

    private val appContext: Context = application.applicationContext


    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val currentUserId: String
        get() = accountService.currentUserId


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


    init {
        observeNotes()
    }




    private  fun observeNotes() {

        viewModelScope.launch {

            withContext(Dispatchers.IO) {

                val notesFlow = if (isNetworkAvailable(appContext )) {
                    storageService.subscribeToRealtimeUpdates()
                        .map { HomeUiState(it) }
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                            initialValue = HomeUiState()
                        )
                } else {
                    notesRepository.getAllUserNotes(currentUserId)
                        .map { HomeUiState(it) }
                        .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                            initialValue = HomeUiState()
                        )
                }

                withContext(Dispatchers.Main){
                    notesFlow.collectLatest {
                        _homeUiState.value = it
                        _isLoading.value = false
                    }
                }

            }

        }


    }



//    val homeUiState: StateFlow<HomeUiState> =
//        notesRepository.getAllNotesStream().map { HomeUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = HomeUiState()
//            )


    fun signOut(navController: NavController) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accountService.signOut()
            }
            withContext(Dispatchers.Main){
                navController.navigate(LoginDestination.route) {
//                    popUpTo(HomeDestination.route) { inclusive = true }
                    popUpTo(navController.graph.id){
                        inclusive = true
                    }
                }
            }
        }

    }


    fun deleteAccount(navController: NavController) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                storageService.deleteAllUserData()
                notesRepository.clearLocalUserData(currentUserId)
                accountService.deleteAccount()
            }
            withContext(Dispatchers.Main){
                navController.navigate(LoginDestination.route) {
//                    popUpTo(HomeDestination.route) { inclusive = true }
                    popUpTo(navController.graph.id){
                        inclusive = true
                    }
                }
            }
        }

    }
}


data class HomeUiState(
    val itemList: List<Note> = listOf(),
    val message: String = ""
)

//sealed class HomeUiState {
//    data object Idle : HomeUiState()
//    data object Loading : HomeUiState()
//    data class Success(val itemList: List<Note>) : HomeUiState()
//}
