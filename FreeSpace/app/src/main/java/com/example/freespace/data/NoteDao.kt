package com.example.freespace.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(notes: List<Note>)
//
//    @Update
//    suspend fun updateAll(notes: List<Note>)
    @Query("SELECT * FROM notes WHERE userId = :id AND operationType IS NOT 'DELETE' ORDER BY createdAt DESC")
    fun getAllUserNotes(id: String): Flow<List<Note>>


//    @Query("DELETE FROM notes WHERE isSynced = 1 AND (id IS NULL OR id = '')")
//    suspend fun deleteSyncedNotesWithEmptyServerId()

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM notes WHERE userId = :userId ")
    suspend fun clearLocalUserData(userId: String)


    @Query("SELECT * FROM notes WHERE roomId = :id  LIMIT 1")
    fun getNote(id: Int): Flow<Note?>

//    @Query("SELECT * FROM notes ORDER BY createdAt ASC")
//    fun getAllNotes(): Flow<List<Note>>


//    @Query("DELETE FROM notes WHERE isSynced = 1")
//    suspend fun deleteSyncedNotes()

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<Note>



}