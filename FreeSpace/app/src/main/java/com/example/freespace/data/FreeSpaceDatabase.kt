package com.example.freespace.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class FreeSpaceDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
}