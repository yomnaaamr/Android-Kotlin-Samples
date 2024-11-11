package com.example.freespace.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freespace.data.Note
import com.example.freespace.data.NotesRepository
import com.example.freespace.firebase.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository,
    private val storageService: StorageService
) : ViewModel() {


    var itemUiState by mutableStateOf(NoteUiState())
        private set


    private val noteId: Int = checkNotNull(savedStateHandle[NoteDetailsDestination.noteIdArg])


    private val _isNoteItemClicked = MutableStateFlow(false)
    val isNoteItemClicked = _isNoteItemClicked.asStateFlow()
    fun onTextFieldClicked() {
        _isNoteItemClicked.value = true
    }

//    companion object {
//        private const val TIMEOUT_MILLIS = 5_000L
//    }

//    val uiState: StateFlow<NoteDetailsUiState> =
//        notesRepository.getNoteStream(noteId)
//            .filterNotNull()
//            .map {
//                NoteDetailsUiState(noteDetails = it.toNoteDetails())
//            }.stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = NoteDetailsUiState()
//            )


    init {

        viewModelScope.launch {
           itemUiState =
               notesRepository.getNoteStream(noteId)
                .filterNotNull()
                .first()
                .toNoteUiState()
        }
    }

    fun updateUiState(noteDetails: NoteDetails){

        itemUiState = NoteUiState(noteDetails = noteDetails,
            isEntryValid = validateInput(noteDetails))
   }


    fun validateInput(noteDetails: NoteDetails = itemUiState.noteDetails): Boolean {
        return with(noteDetails){
            title.isNotBlank() || note.isNotBlank()
        }

    }


    suspend fun updateNote(){
        if (validateInput(itemUiState.noteDetails)){
            notesRepository.updateNote(itemUiState.noteDetails.toNote())
        }
    }


    suspend fun deleteNote(){
        notesRepository.deleteNote(itemUiState.noteDetails.toNote())
    }


}


//data class NoteDetailsUiState(
//    var noteDetails: NoteDetails = NoteDetails()
//)


fun Note.toNoteUiState(isEntryValid: Boolean = false) : NoteUiState = NoteUiState(
    isEntryValid = isEntryValid,
    noteDetails = this.toNoteDetails()
)

fun Note.toNoteDetails(): NoteDetails = NoteDetails(
    id = this.roomId,
    title = title,
    note = note,
    data = getTimeString(createdAt).first,
    time = getTimeString(createdAt).second
)