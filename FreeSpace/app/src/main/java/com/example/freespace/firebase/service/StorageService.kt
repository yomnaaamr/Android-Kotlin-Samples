package com.example.freespace.firebase.service

import com.example.freespace.data.Note
import com.example.freespace.firebase.FirebaseNote
import kotlinx.coroutines.flow.Flow

interface StorageService {

    val notes: Flow<List<FirebaseNote>>
    suspend fun getNote(noteId: String): FirebaseNote?
    suspend fun save(note: FirebaseNote)

    //    suspend fun fetchNotes(): Flow<List<Note>>

    suspend fun deleteAllUserData()
    suspend fun subscribeToRealtimeUpdates(): Flow<List<Note>>

    suspend fun update(note: FirebaseNote)
    suspend fun delete(noteId: String)

}