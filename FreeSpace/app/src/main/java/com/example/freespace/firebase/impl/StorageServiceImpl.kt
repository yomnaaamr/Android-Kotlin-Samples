package com.example.freespace.firebase.impl

import com.example.freespace.data.Note
import com.example.freespace.data.NoteDao
import com.example.freespace.data.toRoomNote
import com.example.freespace.firebase.FirebaseNote
import com.example.freespace.firebase.service.AccountService
import com.example.freespace.firebase.service.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class StorageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
    private val noteDao: NoteDao
) : StorageService{

    private val userCollection get() = firestore.collection(USER_COLLECTION)
        .document(auth.currentUserId).collection(NOTE_COLLECTION)


    @OptIn(ExperimentalCoroutinesApi::class)
    override val notes: Flow<List<FirebaseNote>>
        get() = auth.currentUser.flatMapLatest { user ->
            firestore
                .collection(USER_COLLECTION)
                .document(user.id)
                .collection(NOTE_COLLECTION)
                .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
                .dataObjects()
        }



    override suspend fun subscribeToRealtimeUpdates(): Flow<List<Note>> = callbackFlow {
        if (auth.currentUserId.isEmpty()) {
            cancel("Current user ID is null or empty")
            return@callbackFlow
        }
        val listenerRegistration = userCollection.addSnapshotListener { value, error ->
            error?.let {
                cancel("Error fetching notes", it)
                return@addSnapshotListener
            }

            value?.let { querySnapshot ->

                val firebaseNotes = querySnapshot.toObjects(FirebaseNote::class.java)

                CoroutineScope(Dispatchers.IO).launch {

                    firebaseNotes.forEach { firebaseNote ->
                        val roomNote = firebaseNote.toRoomNote()

                        val existingNote = noteDao.getNote(roomNote.roomId).first()
                        if (existingNote?.roomId.toString() != firebaseNote.id) {
                            noteDao.insert(roomNote)
                        }

                    }

                    val updatedNotes = noteDao.getAllUserNotes(auth.currentUserId).first()
//                        .filter { it.operationType != "DELETE" }
                    trySend(updatedNotes).isSuccess

                }
            }
        }

        awaitClose { listenerRegistration.remove() }
    }

//    override suspend fun fetchNotes(): Flow<List<Note>> {
//
//        return withContext(Dispatchers.IO) {
//
//            val initialSnapshot = firestore.collection(StorageServiceImpl.USER_COLLECTION)
//                .document(auth.currentUserId)
//                .collection(StorageServiceImpl.NOTE_COLLECTION)
//                .get()
//                .await()
//
//            val initialFirebaseNotes = initialSnapshot.toObjects(FirebaseNote::class.java)
//            // Check if each note already exists in Room
//            val existingNotes =
//                noteDao.getAllUserNotes(auth.currentUserId).associateBy { it.roomId }
//
//            // Prepare a list of new notes to insert
//            val newNotes = initialFirebaseNotes.mapNotNull { firebaseNote ->
//                val roomNote =
//                    firebaseNote.toRoomNote().copy(isSynced = true, userId = auth.currentUserId)
//                if (existingNotes[roomNote.roomId] == null) roomNote else null
//            }
//
//            // Insert only the new notes
//            if (newNotes.isNotEmpty()) {
//                noteDao.insertAll(newNotes)
//            }
//    }




    override suspend fun getNote(noteId: String): FirebaseNote? =
        userCollection.document(noteId).get().await().toObject()

    override suspend fun save(note: FirebaseNote) {
        userCollection.document(note.id).set(note).await()
    }

    override suspend fun deleteAllUserData() {
        val userNotes = userCollection.get().await()
        for (document in userNotes.documents) {
            delete(document.id)
        }
    }


    override suspend fun update(note: FirebaseNote) {
        userCollection.document(note.id).set(note).await()

    }


    override suspend fun delete(noteId: String) {
        userCollection.document(noteId).delete().await()
    }



    companion object {
        const val USER_COLLECTION = "users"
        private const val CREATED_AT_FIELD = "createdAt"
        const val NOTE_COLLECTION = "notes"
    }
}