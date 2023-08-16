package com.example.bookreview.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val bookName: String,
    @ColumnInfo(name = "author")
    val bookAuthor: String,
    @ColumnInfo(name = "num_pages")
    val bookNumPage: String,
    @ColumnInfo(name = "review")
    val bookReview: String,
    @ColumnInfo(name = "rate")
    val bookRate: Int = -1

)
