package com.example.orgauns.data.repository

import com.example.orgauns.domain.model.Note
import com.example.orgauns.domain.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : NoteRepository {

    private fun getUserNotesCollection() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("notes")
    }

    override fun getNotes(): Flow<List<Note>> = callbackFlow {
        val collection = getUserNotesCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val notes = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Note::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(notes)
        }
        awaitClose { listener.remove() }
    }

    override fun getNoteById(noteId: String): Flow<Note?> = callbackFlow {
        val collection = getUserNotesCollection()
        if (collection == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = collection.document(noteId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            val note = snapshot?.toObject(Note::class.java)?.copy(id = snapshot.id)
            trySend(note)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createNote(note: Note): Result<String> {
        return try {
            val collection = getUserNotesCollection()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val docRef = collection.document()
            val noteWithTimestamp = note.copy(
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            docRef.set(noteWithTimestamp).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(note: Note): Result<Unit> {
        return try {
            val collection = getUserNotesCollection()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val noteWithTimestamp = note.copy(updatedAt = System.currentTimeMillis())
            collection.document(note.id).set(noteWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            val collection = getUserNotesCollection()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            collection.document(noteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

