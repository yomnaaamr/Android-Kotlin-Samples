package com.example.topstories.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.topstories.database.NewsDatabase
import com.example.topstories.network.ResultsItem
import com.example.topstories.repository.NewsRepository
import kotlinx.coroutines.launch




class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(NewsDatabase.getDatabase(application))

    private val _news = MutableLiveData<List<ResultsItem?>?>()
    val news = repository.news



    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError



    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    init {
        refreshDataFromRepository()
    }

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                 repository.refreshNews()
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            }catch (e : Exception){
                // Show a Toast error message and hide the progress bar.
                if(_news.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }


    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

}