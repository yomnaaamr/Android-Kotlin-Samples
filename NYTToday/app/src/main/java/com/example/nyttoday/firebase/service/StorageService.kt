package com.example.nyttoday.firebase.service

import com.example.nyttoday.firebase.FirebaseNewItem
import com.google.firebase.database.DatabaseReference

interface StorageService {


     val userRef: DatabaseReference
     val newsCollectionRef: DatabaseReference
     val savedNewsCollectionRef: DatabaseReference

    suspend fun getStory(storyId: String): FirebaseNewItem?
    suspend fun saveStory(story: FirebaseNewItem)

//    suspend fun getSavedStories(): List<FirebaseNewItem>

    suspend fun saveArticle(uri: String)

    suspend fun isNewsItemExists(uri: String?): Boolean

    suspend fun deleteAllUserData()

    suspend fun delete(noteId: String)


}