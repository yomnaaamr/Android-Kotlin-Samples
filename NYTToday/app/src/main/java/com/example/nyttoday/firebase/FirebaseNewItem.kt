package com.example.nyttoday.firebase

/**
 * In Firebase Realtime Database, there is no direct equivalent to Firestore's @DocumentId annotation
 * because Realtime Database uses a different data model (tree structure) rather than documents and collections.
 */
data class FirebaseNewItem(
    val createdAt: Long = System.currentTimeMillis(),
    val title: String? = "",
    val abstract: String? = "",
    val uri: String? = "",
    val url: String? = "",
    val byline: String? = "",
    val imageUrl: String? = "",
    val imageCopyRight: String? = ""
)