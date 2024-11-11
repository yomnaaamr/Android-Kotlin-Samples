package com.example.nyttoday.firebase.impl

import com.example.nyttoday.firebase.FirebaseNewItem
import com.example.nyttoday.firebase.service.AccountService
import com.example.nyttoday.firebase.service.StorageService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class StorageServiceImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val accountService: AccountService
) : StorageService {

    override val userRef: DatabaseReference
        get() = database.getReference("users").child(accountService.currentUserId)
    override val newsCollectionRef: DatabaseReference
        get() = userRef.child("news")
    override val savedNewsCollectionRef: DatabaseReference
        get() = userRef.child("savedNews")


    override suspend fun getStory(storyId: String): FirebaseNewItem? {
        TODO("Not yet implemented")
    }

    override suspend fun saveStory(story: FirebaseNewItem) {
//        val newsId = newsCollectionRef.push().key ?: return // Generate a unique ID
//        val newsItemWithId = story.copy(id = newsId)
//        newsCollectionRef.child(newsId).setValue(newsItemWithId).await()

        val newsId =
            story.uri?.hashCode().toString() // Generate a unique ID based on URI's hash code
        newsCollectionRef.child(newsId).setValue(story).await()
    }



    private fun getNewsByURL(url: String, callback: (FirebaseNewItem?) -> Unit) {
        newsCollectionRef.orderByChild("url").equalTo(url).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val newsItemSnapshot = dataSnapshot.children.firstOrNull()
                    if (newsItemSnapshot != null) {
                        val newsItem = newsItemSnapshot.getValue(FirebaseNewItem::class.java)
                        callback(newsItem)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    override suspend fun saveArticle(uri: String) {

        withContext(Dispatchers.IO) {


            getNewsByURL(uri) { newsItem ->
                if (newsItem != null) {
                    val newsId = newsItem.uri?.hashCode().toString()
                    savedNewsCollectionRef.child(newsId).setValue(newsItem)
                }
            }
        }

    }

    override suspend fun isNewsItemExists(uri: String?): Boolean =
        suspendCoroutine { continuation ->
            if (uri == null) {
                continuation.resume(false)
                return@suspendCoroutine
            }

            newsCollectionRef.orderByChild("uri").equalTo(uri)
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot.exists())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }


    override suspend fun deleteAllUserData() {
        savedNewsCollectionRef.removeValue().await()
        newsCollectionRef.removeValue().await()
    }


//    not recommended

//    private fun clearFirebaseCache() {
//        // Temporarily disable persistence
//        FirebaseDatabase.getInstance().setPersistenceEnabled(false)
//
//        // Re-enable persistence (this clears the local cache)
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
//    }

    override suspend fun delete(noteId: String) {
        TODO("Not yet implemented")
    }
}