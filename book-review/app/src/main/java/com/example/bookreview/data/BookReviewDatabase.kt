package com.example.bookreview.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class BookReviewDatabase : RoomDatabase() {

    abstract fun bookDao() : BookDao

    companion object{
        @Volatile
        private var INSTANCE : BookReviewDatabase? = null

        fun getDatabase(context: Context) : BookReviewDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookReviewDatabase::class.java,
                    "book_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}