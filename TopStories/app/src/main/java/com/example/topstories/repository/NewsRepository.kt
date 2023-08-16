package com.example.topstories.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.topstories.database.NewsDatabase
import com.example.topstories.database.asDomainModel
import com.example.topstories.domain.AppNewsModel
import com.example.topstories.network.NewsApi
import com.example.topstories.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository (private val database: NewsDatabase){

    val news : LiveData<List<AppNewsModel>> = (database.newsDao().getNews()).map {
        it.asDomainModel()
    }

    suspend fun refreshNews(){
        withContext(Dispatchers.IO){
            val news = NewsApi.retrofitService.getNews()
            database.newsDao().insertAll(news.asDatabaseModel())
        }
    }


    suspend fun deleteOldNews(){
        withContext(Dispatchers.IO){
            database.newsDao().deleteNewsBeforeToday()
        }
    }
}