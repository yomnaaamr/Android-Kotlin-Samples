package com.example.nyttoday.repository

import com.example.nyttoday.data.Article
import com.example.nyttoday.data.NYTResponse
import com.example.nyttoday.util.Resource
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun fetchedNewsFromApi(): Resource<NYTResponse>
//    suspend fun fetchedNewsFromApi(): List<ResultsItem>

    suspend fun fetchNewsAndStoreThem()

    suspend fun observeDataWithListener():  Flow<List<Article>>

//    suspend fun fetchDataFromFirebase(): List<ResultsItem>

    suspend fun refreshNewsEvery12Hours()


    suspend fun saveArticle(url: String)


    suspend fun fetchSavedArticles():  Flow<List<Article>>

//    fun getNewsFromRealtimeDatabase(): Flow<PagingData<FirebaseNewItem>>
//    fun getNewsFromRealtimeDatabase(): PagingSource<String, FirebaseNewItem>

//    suspend fun fetchDataWithPaging( limit: Int, offset: Int) : List<ResultsItem>

    suspend fun clearAllDate()


    suspend fun getLastSnapshotForKey(key: String): DataSnapshot?
}