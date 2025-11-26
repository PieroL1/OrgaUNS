package com.example.orgauns.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orgauns.data.repository.NoteRepositoryImpl
import com.example.orgauns.domain.model.Note
import com.example.orgauns.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotesViewModel(
    private val noteRepository: NoteRepositoryImpl = NoteRepositoryImpl()
) : ViewModel() {

    private val getNotesUseCase = GetNotesUseCase(noteRepository)
    private val createNoteUseCase = CreateNoteUseCase(noteRepository)
    private val updateNoteUseCase = UpdateNoteUseCase(noteRepository)
    private val deleteNoteUseCase = DeleteNoteUseCase(noteRepository)

    private val _uiState = MutableStateFlow(NotesUiState(isLoading = true))
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getNotesUseCase()
                .catch { e ->
                    _uiState.value = NotesUiState(error = e.message ?: "Error al cargar notas")
                }
                .collect { notes ->
                    _uiState.value = NotesUiState(notes = notes)
                }
        }
    }

    fun createNote(note: Note) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            createNoteUseCase(note)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al crear nota"
                    )
                }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase(note)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

