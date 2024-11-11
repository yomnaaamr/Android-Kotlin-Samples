package com.example.nyttoday.ui.savedItems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyttoday.data.Article
import com.example.nyttoday.repository.Repository
import com.example.nyttoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class SavedScreenViewModel @Inject constructor(
    private val repository: Repository,
    ): ViewModel()
{

    private val _savedUiState = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val savedUiState: StateFlow<Resource<List<Article>>> = _savedUiState.asStateFlow()


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


    init {
        fetchSavedData()
    }

    private fun fetchSavedData() {
        _savedUiState.value = Resource.Loading()
        viewModelScope.launch {
            repository.fetchSavedArticles()
                .map { Resource.Success(it) }
                .catch {
                    if(it is CancellationException){
                        throw it
                    }
                    _savedUiState.value = Resource.Error(it.message.toString())
                }
//                .stateIn(
//                    scope = viewModelScope,
//                        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                        initialValue = HomeUiState.Loading
//                )
                .collectLatest {articles->
                        _savedUiState.value = articles

                }
        }
    }

}