package com.example.freespace.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val roomId: Int = 0,
    /**
     * Using UUID.randomUUID().toString() as the primary key for your Note entity ensures
     * that the same unique ID is used across both Room and Firestore.
     * This approach provides simplicity and consistency in ID management.
     *
     * it does not work LOL!! , so i back to room autoGenerate id.
     */
//    @PrimaryKey
//    val roomId: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val note: String,
    val time: String,
    val date: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val operationType: String = "INSERT"
)
