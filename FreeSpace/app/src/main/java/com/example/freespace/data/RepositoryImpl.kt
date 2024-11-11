package com.example.freespace.data

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.freespace.firebase.FirebaseNote
import com.example.freespace.firebase.service.StorageService
import com.example.freespace.ui.login.isNetworkAvailable
import com.example.freespace.workmanager.SyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val workManager: WorkManager,
    private val context: Context,
    private val storageService: StorageService
) : NotesRepository {
    override suspend fun clearLocalUserData(userId: String) {
        return noteDao.clearLocalUserData(userId)
    }


    override fun getAllUserNotes(id: String): Flow<List<Note>> {
        return noteDao.getAllUserNotes(id)
//            .map { notes-> listOf(notes) }
//            .asFlow()
    }



    override fun getNoteStream(id: Int): Flow<Note?> {
        return noteDao.getNote(id)
    }

    override suspend fun insertNote(note: Note) {
        val updatedNote = note.copy(isSynced = false, operationType = "INSERT")
        noteDao.insert(updatedNote)
        if (isNetworkAvailable(context)) {
            syncNotes()
        } else {
            scheduleSyncJob()
        }
    }

    override suspend fun deleteNote(note: Note) {
        val updatedNote = note.copy(isSynced = false, operationType = "DELETE")
        noteDao.update(updatedNote)
        if (isNetworkAvailable(context)) {
            syncNotes()
        } else {
            scheduleSyncJob()
        }
    }

    override suspend fun updateNote(note: Note) {
        val updatedNote = note.copy(isSynced = false, operationType = "UPDATE")
        noteDao.update(updatedNote)
        if (isNetworkAvailable(context)) {
            syncNotes()
        } else {
            scheduleSyncJob()
        }

    }

    override suspend fun getUnsyncedNotes(): List<Note> {
        return noteDao.getUnsyncedNotes()
    }

    suspend fun syncNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            val unsyncedNotes = noteDao.getUnsyncedNotes()
            unsyncedNotes.forEach { roomNote ->
                try {
                    /**
                     * in Firestore, there are differences between using set and add when adding documents to a collection:
                     * add: Automatically generates a unique ID for the document and sets the document's data.
                     * set: Allows you to specify the document ID yourself and set the document's data.
                     */
                    val firebaseNote = roomNote.toFirebaseNote()
                    when (roomNote.operationType) {
                        "INSERT" -> {
//                            val firebaseNote = roomNote.toFirebaseNote()
//                            userCollection.add(firebaseNote).await()
//                            val newDocRef = userCollection
//                                    .add(firebaseNote)
//                                    .await()
//                            val serverId = newDocRef.id
                            // Update the note with the server ID
//
//                            userCollection.document(roomNote.roomId.toString()).set(firebaseNote)
//                                .await()
                            storageService.save(firebaseNote)
                        }

                        "UPDATE" -> {
//                            val firebaseNote = roomNote.toFirebaseNote()
//                            /** do not need it , we can pass the firebase object itself **/
//                            val updates = mapOf<String, Any?>(
//                                "createdAt" to firebaseNote.createdAt,
//                                "date" to firebaseNote.date,
//                                "note" to firebaseNote.note,
//                                "time" to firebaseNote.time,
//                                "title" to firebaseNote.title
//                            )
//                            userCollection.document(roomNote.roomId.toString())
//                                .set(updates,
////                              ensures only the provided fields are updated, leaving other document fields untouched.
//                                    SetOptions.merge()
//                                ).await()

//                            userCollection.document(roomNote.roomId.toString())
//                                .set(firebaseNote).await()
                            storageService.update(firebaseNote)
                        }

                        "DELETE" -> {
//                            userCollection.document(roomNote.roomId.toString()).delete().await()
                            storageService.delete(firebaseNote.id)
                            noteDao.delete(roomNote)
                        }
                    }
                    noteDao.update(roomNote.copy(isSynced = true))
                } catch (e: Exception) {
                    Log.e("RepositoryImpl", "Error syncing note: ${roomNote.roomId}", e)
                }
            }
        }
    }


    private fun scheduleSyncJob() {
        // Enqueue a WorkManager job to sync data once
        val syncWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueue(syncWorkRequest)
    }


    // Enqueue a WorkManager job to sync data every 12 hours

//    private fun scheduleSyncJob() {
//        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS)
//            .build()
//
//        workManager.enqueueUniquePeriodicWork(
//            "SyncWork",
//            ExistingPeriodicWorkPolicy.KEEP,
//            syncWorkRequest
//        )
//    }


}

fun Note.toFirebaseNote(): FirebaseNote {
    return FirebaseNote(
        id = this.roomId.toString(),
        title = this.title,
        note = this.note,
        time = this.time,
        date = this.date,
        createdAt = Date(this.createdAt),
        userId = this.userId
    )
}


fun FirebaseNote.toRoomNote(): Note {
    return Note(
        roomId = this.id.toInt(),
        title = this.title,
        note = this.note,
        time = this.time,
        date = this.date,
        createdAt = this.createdAt?.time ?: System.currentTimeMillis(),
        isSynced = true,
        userId = this.userId
    )
}