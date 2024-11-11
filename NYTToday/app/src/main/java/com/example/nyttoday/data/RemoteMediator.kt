//package com.example.nyttoday.data
//
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//import com.example.nyttoday.firebase.FirebaseNewItem
//import com.example.nyttoday.firebase.service.StorageService
//import com.example.nyttoday.repository.Repository
//import retrofit2.HttpException
//import java.io.IOException
//
//
//
//@OptIn(ExperimentalPagingApi::class)
//class RemoteMediator(
//    private val repository: Repository,
//    private val storageService: StorageService,
//) : RemoteMediator<String, FirebaseNewItem>() {
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<String, FirebaseNewItem>
//    ): MediatorResult {
//        return try {
//            val loadKey = when (loadType) {
//                LoadType.REFRESH -> 0
//                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
//                LoadType.APPEND -> state.lastItemOrNull()?.uri?.hashCode()?.div(state.config.pageSize) ?: 0
//            }
//
//            val news = repository.fetchDataWithPaging(
//                offset = loadKey,
//                limit = state.config.pageSize
//            )
//
//            if (loadType == LoadType.REFRESH) {
//                repository.clearAllDate()
//            }
//
//            val newsList = news.map { it.toFirebaseNewItem() }
//
//            newsList.forEach { article ->
//                if (!storageService.isNewsItemExists(article.uri)) {
//                    storageService.saveStory(article)
//                }
//            }
//
//            MediatorResult.Success(endOfPaginationReached = news.isEmpty())
//
//        } catch (e: IOException) {
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            MediatorResult.Error(e)
//        }
//    }
//}
//
//
//
//
//
////@OptIn(ExperimentalPagingApi::class)
////class RemoteMediator(
////    private val repository: Repository,
////    private val storageService: StorageService,
////) : RemoteMediator<String, FirebaseNewItem>() {
////
////
////    override suspend fun load(
////        loadType: LoadType,
////        state: PagingState<String , FirebaseNewItem>
////    ): MediatorResult {
////
////        return try {
////
//////            val pageSize = state.config.pageSize
////
//////            val startAfter: DataSnapshot? = when (loadType) {
//////                LoadType.REFRESH -> null // Load initial page
//////                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // No prepend for forward-only paging
//////                LoadType.APPEND -> state.lastItemOrNull()?.let { lastItem ->
//////                    repository.getLastSnapshotForKey(lastItem.uri.hashCode().toString()) // Retrieve the last snapshot for paging
//////                }
//////            }
////
////
////            val loadKey = when (loadType) {
////                LoadType.REFRESH -> 1
////                LoadType.PREPEND -> return MediatorResult.Success(
////                    endOfPaginationReached = true
////                )
////
////                LoadType.APPEND -> {
////                    val lastItem = state.lastItemOrNull()
////                    if (lastItem == null) {
////                        1
////                    } else {
////                        (lastItem.uri.hashCode() / state.config.pageSize) + 1
////                    }
////                }
////            }
////
////
////            val news = repository.fetchDataWithPaging(
//////                page = startAfter?.key?.toInt() ?: 0,
////                page = loadKey,
////                pageSize = state.config.pageSize
////            )
////
////
////            if (loadType == LoadType.REFRESH) {
////                repository.clearAllDate()
////            }
////
////            val newsList = news.map { it.toFirebaseNewItem() }
////
////            newsList.forEach { article ->
////                if (!storageService.isNewsItemExists(article.uri)) {
////                    storageService.saveStory(article)
////                }
////            }
////
////
////            MediatorResult.Success(
////                endOfPaginationReached = news.isEmpty()
////            )
////
////
////        } catch (e: IOException) {
////            MediatorResult.Error(e)
////        } catch (e: HttpException) {
////            MediatorResult.Error(e)
////        }
////
////    }
////}