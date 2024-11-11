package com.example.freespace.data

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

//    fun getAllNotesStream(): Flow<List<Note>>

    suspend fun clearLocalUserData(userId: String)

    fun getAllUserNotes(id: String): Flow<List<Note>>

//    suspend fun deleteSyncedNotesWithEmptyServerId()

//    suspend fun insertAll(notes: List<Note>)
//
//    suspend fun updateAll(notes: List<Note>)

    fun getNoteStream(id: Int): Flow<Note?>

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)
//    suspend fun deleteSyncedNotes()

    suspend fun getUnsyncedNotes(): List<Note>


}