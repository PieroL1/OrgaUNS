package com.example.orgauns.domain.repository

import com.example.orgauns.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    fun getNoteById(noteId: String): Flow<Note?>
    suspend fun createNote(note: Note): Result<String>
    suspend fun updateNote(note: Note): Result<Unit>
    suspend fun deleteNote(noteId: String): Result<Unit>
}

