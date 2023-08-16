package com.example.bookreview

import android.app.Application
import com.example.bookreview.data.BookReviewDatabase

class BookReviewApplication : Application() {

    val database : BookReviewDatabase by lazy {
        BookReviewDatabase.getDatabase(this)
    }
}