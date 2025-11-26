package com.example.orgauns.domain.usecase

import com.example.orgauns.domain.model.Note
import com.example.orgauns.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getNotes()
}

class CreateNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Result<String> = repository.createNote(note)
}

class UpdateNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): Result<Unit> = repository.updateNote(note)
}

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: String): Result<Unit> = repository.deleteNote(noteId)
}

