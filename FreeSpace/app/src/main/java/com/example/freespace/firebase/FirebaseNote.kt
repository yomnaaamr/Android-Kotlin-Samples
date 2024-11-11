package com.example.freespace.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class FirebaseNote(
    @DocumentId val id: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    val title: String = "",
    val note: String,
    val time: String,
    val date: String,
    val userId: String = ""
) {
    // Default constructor needed by Firestore
    constructor() : this("", null, "", "", "", "", "")
}
