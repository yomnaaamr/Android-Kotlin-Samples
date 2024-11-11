package com.example.nyttoday.repository

import com.example.nyttoday.data.Article
import com.example.nyttoday.data.NYTResponse
import com.example.nyttoday.data.NewsApi
import com.example.nyttoday.data.toArticle
import com.example.nyttoday.data.toFirebaseNewItem
import com.example.nyttoday.firebase.FirebaseNewItem
import com.example.nyttoday.firebase.service.AccountService
import com.example.nyttoday.firebase.service.StorageService
import com.example.nyttoday.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: NewsApi,
    private val accountService: AccountService,
    private val storageService: StorageService
) : Repository {



    override suspend fun fetchedNewsFromApi(): Resource<NYTResponse> {

        val response = api.getTopStories()
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    override suspend fun fetchNewsAndStoreThem() {
//        withContext(Dispatchers.IO) {
            val nytResponse = fetchedNewsFromApi()
//            val newsItems = nytResponse.map { it.toFirebaseNewItem()  }

            val newsItems = nytResponse.data?.let { newsResponse->
                newsResponse.results?.mapNotNull { it?.toFirebaseNewItem() } ?: emptyList()

            }

            newsItems?.forEach { newsItem ->
                if (!storageService.isNewsItemExists(newsItem.uri)) {
                    storageService.saveStory(newsItem)
                }
            }
//        }

    }


    override suspend fun saveArticle(url: String) {

        withContext(Dispatchers.IO){
            storageService.saveArticle(url)
        }

    }


    override suspend fun fetchSavedArticles(): Flow<List<Article>> =
        callbackFlow {

            if (!accountService.hasUser) {
                close()
                return@callbackFlow
            }

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val newsItems = snapshot.children.mapNotNull { it.getValue(FirebaseNewItem::class.java) }
                    val resultsItems = newsItems.map { it.toArticle() }
                    trySend(resultsItems.reversed())
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(emptyList())
                    close(error.toException())

                }
            }

            storageService.savedNewsCollectionRef.orderByChild("createdAt")
                .addValueEventListener(listener)
//                .addListenerForSingleValueEvent(listener)
            awaitClose { storageService.savedNewsCollectionRef.removeEventListener(listener) }


        }.flowOn(Dispatchers.IO)



//    override fun getNewsFromRealtimeDatabase(): PagingSource<String, FirebaseNewItem> {
//        return PagingResource(storageService)
//    }


//    override fun getNewsFromRealtimeDatabase(): PagingSource<DataSnapshot, FirebaseNewItem> {
//        return object : PagingSource<DataSnapshot, FirebaseNewItem>(){
//
//            override fun getRefreshKey(state: PagingState<DataSnapshot, FirebaseNewItem>): DataSnapshot? {
//                // Return null to refresh from the start
//                return null
//            }
//
//            override suspend fun load(params: LoadParams<DataSnapshot>): LoadResult<DataSnapshot, FirebaseNewItem> {
//                return try {
//                    val query = storageService.newsCollectionRef
//                        .orderByChild("createdAt")
//                        .limitToFirst(params.loadSize)
//
//                    val currentPage = params.key?.let {
//                        query.startAfter(it.key).get().await() // Start after the last item for the next page
//                    } ?: query.get().await() // Initial load
//
//                    val newsList = currentPage.children.mapNotNull { it.getValue(FirebaseNewItem::class.java) }
//                    val lastVisiblePost = currentPage.children.lastOrNull()
//
//                    LoadResult.Page(
//                        data = newsList,
//                        prevKey = null,  // Only paging forward
//                        nextKey = lastVisiblePost // Set the next key to the last item fetched
//                    )
//                } catch (e: Exception) {
//                    LoadResult.Error(e)
//                }
//            }
//        }
//    }





//    override fun getNewsFromRealtimeDatabase(): PagingSource<DataSnapshot, FirebaseNewItem> {
//
//        return object : PagingSource<DataSnapshot, FirebaseNewItem>() {
//
//            override fun getRefreshKey(state: PagingState<DataSnapshot, FirebaseNewItem>): DataSnapshot? = null
//
//            override suspend fun load(params: LoadParams<DataSnapshot>): LoadResult<DataSnapshot, FirebaseNewItem> = try {
//
//                val query = storageService.newsCollectionRef.orderByChild("createdAt").limitToFirst(20)
//                val currentPage = params.key ?: query.get().await()
//                val lastVisiblePost = currentPage.children.lastOrNull()
//                val nextPage = lastVisiblePost?.let { query.startAfter(it.key).get().await() }
//
//                val news = currentPage.children
//
//                LoadResult.Page(
//                    data = news.mapNotNull { it.getValue(FirebaseNewItem::class.java) },
//                    prevKey = null,
//                    nextKey = nextPage
//                )
//
//            }catch (e: Exception){
//                LoadResult.Error(e)
//            }
//
//        }
//
//    }




//
//    override suspend fun fetchDataWithPaging(limit: Int, offset: Int): List<ResultsItem> {
//
//        return withContext(Dispatchers.IO) {
//            try {
//                api.getNewsByPaging(offset = offset, limit = limit).results?.filterNotNull() ?: emptyList()
//            } catch (e: Exception) {
//                emptyList()
//            }
//        }
//    }


    override suspend fun observeDataWithListener(): Flow<List<Article>> =
        callbackFlow {

        if (!accountService.hasUser) {
//            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val newsItems =
                    snapshot.children.mapNotNull { it.getValue(FirebaseNewItem::class.java) }
                val resultsItems = newsItems.map { it.toArticle() }
                trySend(resultsItems.reversed())
//                emit(resultsItems.reversed())

                /**
                 * By using resultsItems.reversed(), the list of news items will be ordered in descending order based on their createdAt timestamp.
                 */
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
                close(error.toException())

            }
        }

        storageService.newsCollectionRef.orderByChild("createdAt")
//            .addListenerForSingleValueEvent(listener)
            .addValueEventListener(listener)
        awaitClose { storageService.newsCollectionRef.removeEventListener(listener) }


    }
//            .flowOn(Dispatchers.IO)


//    @SuppressLint("RestrictedApi")
//    override suspend fun fetchDataFromFirebase(): List<ResultsItem> = withContext(Dispatchers.IO) {
//        if (!accountService.hasUser) {
//            return@withContext emptyList()
//        }
//
//        try {
//            val snapshot = storageService.newsCollectionRef.orderByChild("createdAt").get().result
////                .get().await()  // Fetch the data once
//
//            val newsItems = snapshot.children.mapNotNull { it.getValue(FirebaseNewItem::class.java) }
//            val resultsItems = newsItems.map { it.toResultItem() }
//
//            return@withContext resultsItems.reversed()
//        } catch (e: Exception) {
//            return@withContext emptyList()
//        }
//    }


    override suspend fun refreshNewsEvery12Hours() {
        withContext(Dispatchers.IO) {
            clearAllDate()
            fetchNewsAndStoreThem()
            observeDataWithListener()
        }
    }


    override suspend fun clearAllDate(){
        withContext(Dispatchers.IO){
            storageService.newsCollectionRef.removeValue().await()
        }
    }


    override suspend fun getLastSnapshotForKey(key: String): DataSnapshot? {
        val query = storageService.newsCollectionRef.orderByKey().equalTo(key)
        return query.get().await().children.lastOrNull()
    }


}

