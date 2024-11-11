//package com.example.nyttoday.data
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.example.nyttoday.firebase.FirebaseNewItem
//import com.example.nyttoday.firebase.service.StorageService
//import com.google.firebase.database.DataSnapshot
//import kotlinx.coroutines.tasks.await
//
//
//class PagingResource(
//    private val storageService: StorageService
//) : PagingSource<String, FirebaseNewItem>() {
//
//    override fun getRefreshKey(state: PagingState<String, FirebaseNewItem>): String? {
//        return null
//    }
//
//    override suspend fun load(params: LoadParams<String>): LoadResult<String, FirebaseNewItem> {
//        val currentPage = params.key ?: "0" // Start from page 0
//        val nextPage = (currentPage.toInt() + 1).toString()
//
//        var lastVisible: DataSnapshot? = null
//        if (params.key != null) {
//            val prevSnapshot = storageService.newsCollectionRef.orderByKey().limitToLast(1).get().await()
//            lastVisible = prevSnapshot.children.firstOrNull()
//        }
//
//        val itemsRef = storageService.newsCollectionRef
//            .orderByKey()
//            .let {
//                if (lastVisible != null) {
//                    it.startAfter(lastVisible.key)
//                } else {
//                    it
//                }
//            }
//            .limitToFirst(params.loadSize)
//
//        val snapshot = itemsRef.get().await()
//        val items = snapshot.children.mapNotNull { it.getValue(FirebaseNewItem::class.java) }
//
//        return LoadResult.Page(
//            data = items,
//            prevKey = null,
//            nextKey = if (items.size < params.loadSize) null else nextPage
//        )
//    }
//}
//
//
//
//
//class PagingResource(
//    private val storageService: StorageService
//):  PagingSource<String, FirebaseNewItem>() {
//
//    override fun getRefreshKey(state: PagingState<String, FirebaseNewItem>): String? {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun load(params: LoadParams<String>): LoadResult<String, FirebaseNewItem> {
//
//        val currentPage = params.key ?: "0" // Start from page 0
//        val nextPage = (currentPage.toInt() + 1).toString()
//
//
//        val itemsRef = storageService.newsCollectionRef.orderByKey().limitToFirst(params.loadSize)
//        val snapshot = itemsRef.get().await()
//
//        val items = snapshot.children.mapNotNull { it.getValue(FirebaseNewItem::class.java) }
//
//        return LoadResult.Page(
//            data = items,
//            prevKey = null,
//            nextKey = if (items.size < params.loadSize) null else nextPage
//        )
//
////        val key = params.key ?: 0
////
////
////        val query = storageService.newsCollectionRef.orderByChild("uri")
////            .limitToFirst(params.loadSize + 1) // Fetch one extra item to check for next page
////            .startAfter(key.toString()) // Start from the provided or last key
////
////        val snapshot = query.get().await()
////
////        val items = snapshot.children.mapNotNull { it.getValue(FirebaseNewItem::class.java)!! }
////        val nextKey = items.lastOrNull()?.uri // Get the URI of the last item as the next key
////
////        return LoadResult.Page(
////            data = items.dropLast(1), // Remove the last item if it's used as next key
////            prevKey = null, // No previous key for initial load
////            nextKey = if (items.size > params.loadSize) nextKey else null // Only set next key if there are more items
////        )
//
//    }
//}