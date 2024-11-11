package com.example.freespace.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.freespace.data.Note
import com.example.freespace.data.NotesRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NoteEntryViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
) : ViewModel(){

    var noteUiState by mutableStateOf(NoteUiState())
        private set


    fun updateUiState(noteDetails: NoteDetails){
        noteUiState =
            NoteUiState(noteDetails,validateInput(noteDetails))
    }

     fun validateInput(noteDetails: NoteDetails = noteUiState.noteDetails): Boolean {

        return with(noteDetails){
            title.isNotBlank() || note.isNotBlank()
        }

    }


    suspend fun saveNote(){
        if(validateInput()){
            notesRepository.insertNote(noteUiState.noteDetails.toNote())
        }
    }

}



data class NoteUiState(
    var noteDetails: NoteDetails = NoteDetails(),
    val isEntryValid: Boolean = false
)


data class NoteDetails(
    val id: Int = 0,
    val title: String = "",
    val note: String = "",
    val time: String = "",
    val data: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)


fun NoteDetails.toNote(): Note = Note(
    roomId = this.id,
    title = title,
    note = note,
    date = getTimeString(createdAt).first,
    time = getTimeString(createdAt).second,
    userId = Firebase.auth.currentUser!!.uid,

)



fun getTimeString(createdAt: Long): Pair<String, String> {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = createdAt

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-indexed
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY) // Use HOUR_OF_DAY for 24-hour format
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    val formattedDate = String.format("%04d-%02d-%02d", year, month, day)
    val formattedTime = String.format("%02d:%02d:%02d", hour, minute, second)

    return Pair(formattedDate, formattedTime)

}

