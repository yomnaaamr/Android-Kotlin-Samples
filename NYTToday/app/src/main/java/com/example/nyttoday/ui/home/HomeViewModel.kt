package com.example.nyttoday.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyttoday.data.Article
import com.example.nyttoday.repository.Repository
import com.example.nyttoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class HomeViewModel @Inject constructor(
//    private val newsApi: NewsApi
    private val repository: Repository,
//   pager: Pager<String,FirebaseNewItem>
) : ViewModel() {


    private val _homeUiState = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val homeUiState: StateFlow<Resource<List<Article>>> = _homeUiState.asStateFlow()



//    val newsPagingFlow = pager
//        .flow
//        .map { pagingData ->
//            pagingData.map { it.toArticle() }
//        }
//        .cachedIn(viewModelScope)

//    private val _homeUiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
//    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

//    private val _isDataLoaded = MutableStateFlow(false)
//    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    private val _showNavigationBar = MutableStateFlow(true)
    val showNavigationBar: StateFlow<Boolean> = _showNavigationBar

    private val _isShownBottomSheet = MutableStateFlow(false)
    val isShownBottomSheet: StateFlow<Boolean> = _isShownBottomSheet.asStateFlow()


    private val _clickedItemUrl = MutableStateFlow("")
    val clickedItemUrl: StateFlow<String> = _clickedItemUrl.asStateFlow()


    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    init {
        getDate()
    }


    private fun getDate() = viewModelScope.launch {
        fetchInitialData()
    }





    @SuppressLint("SuspiciousIndentation")
    private suspend fun fetchInitialData() {
        _homeUiState.value = Resource.Loading()
//        viewModelScope.launch(Dispatchers.IO) {
            repository.observeDataWithListener()
                .map { Resource.Success(it) }
                .catch {
                    if(it is CancellationException){
                        throw it
                    }
                    _homeUiState.value = Resource.Error(message = it.message ?: "Unknown error")
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
//                    .WhileSubscribed(TIMEOUT_MILLIS)
                    initialValue = Resource.Loading()
                )
                .collectLatest { news ->
                        _homeUiState.value = news
                }
//                .collectLatest {articles->
//                    if (articles.data.isNullOrEmpty()){
//                        repository.fetchNewsAndStoreThem()
//                        repository.observeDataWithListener()
//                            .map { Resource.Success(it) }
//                            .catch {
//                                _homeUiState.value = Resource.Error(message = it.message ?: "Unknown error")
//                            }
//                            .collectLatest {updatedData->
//                                _homeUiState.value =  updatedData
//                            }
//                    }else{
//                        _homeUiState.value = articles
//                    }
//                }
//        }
    }


    fun updateIsShownBottomSheetState(state: Boolean) {
        _isShownBottomSheet.value = state
    }

    fun updateClickedItemUrlState(url: String) {
        _clickedItemUrl.value = url
    }

    fun updateNavigationBarState(newState: Boolean) {
        _showNavigationBar.value = newState
    }


    suspend fun refreshNews() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchNewData()
                _isRefreshing.value = false
            } catch (e: Exception) {
                if(e is CancellationException){
                    throw e
                }
                _homeUiState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }


    private suspend fun fetchNewData() {
        return withContext(Dispatchers.IO) {
            repository.fetchNewsAndStoreThem()
//            fetchInitialData()
            getDate()
        }
    }


    suspend fun saveArticle(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveArticle(url)
        }
    }



//    private fun fetchInitialData() {
//        _homeUiState.value = HomeUiState.Loading
//        viewModelScope.launch {
//            withContext(Dispatchers.IO){
//                try {
//
////                    val cachedNews = repository.fetchDataFromFirebase()
////                    if (cachedNews.isEmpty()) {
////                        repository.fetchNewsAndStoreThem()
////                        repository.observeDataWithListener()
////                        val updatedNews = repository.fetchDataFromFirebase()
////                        _homeUiState.value = HomeUiState.Success(updatedNews)
////                    }else{
////                        _homeUiState.value = HomeUiState.Success(cachedNews)
////                    }
//
//                    // Use a Firebase listener to fetch data
//                    repository.observeDataWithListener()
//                        .collectLatest { newsItems ->
//                            if (newsItems.isEmpty()) {
//                                // Log or handle the case where no news items are found
//                                println("No news items found, fetching new data...")
//                                repository.fetchNewsAndStoreThem()
//                                val updatedNews = repository.observeDataWithListener()
//                                _homeUiState.value = HomeUiState.Success(updatedNews.first())
//                            } else {
//                                _homeUiState.value = HomeUiState.Success(newsItems)
//                            }
//                        }
//                } catch (e: Exception) {
//                    _homeUiState.value = HomeUiState.Error("Error fetching news: ${e.message}")
//                }
//            }
//
//        }
//    }

//    private fun startRealTimeListener() {
//
//        _homeUiState.value = HomeUiState.Loading
//        viewModelScope.launch {
//            try {
//
//                repository.observeDataWithListener()
//                    .map { HomeUiState.Success(it) }
//                    .stateIn(
//                        scope = viewModelScope,
//                        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                        initialValue = HomeUiState.Loading
//                    )
//                    .collectLatest { news ->
//                        _homeUiState.value = news
//                    }
//
//            } catch (e: Exception) {
//                _homeUiState.value = HomeUiState.Error("Error fetching news: ${e.message}")
//            }
//        }
//    }



//    private fun fetchNews() {
//        _homeUiState.value = HomeUiState.Loading
//        viewModelScope.launch {
//            try {
//                withContext(Dispatchers.IO) {
//                    val cachedNews = repository.observeData().first()
//
//                    val newsFlow = if (cachedNews.isEmpty()){
//                        repository.fetchNewsAndStoreThem()
//                        repository.observeData()
//                            .map { HomeUiState.Success(it) }
//                            .stateIn(
//                                scope = viewModelScope,
//                                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                                initialValue = HomeUiState.Loading
//                            )
//                    }else{
//                        repository.observeData()
//                            .map { HomeUiState.Success(it) }
//                            .stateIn(
//                                scope = viewModelScope,
//                                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                                initialValue = HomeUiState.Loading
//                            )
//                    }
//
//
//                    newsFlow.collectLatest {news->
//                        _homeUiState.value = news
//                    }
//
//                }
//
//            } catch (e: Exception) {
//                HomeUiState.Error("Error fetching news: ${e.message}")
//            }
//
//
//        }
//    }



}






sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val newsList: List<Article> = listOf()) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}


