package com.example.bookreview.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM book WHERE id = :id ")
    fun getBook(id : Int) : Flow<Book>

    @Query("SELECT * FROM book ORDER BY num_pages ASC")
    fun getBooks() : Flow<List<Book>>
}