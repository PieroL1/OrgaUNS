package com.example.orgauns.domain.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dueAt: Long? = null,
    val priority: Int = 1, // 1: baja, 2: media, 3: alta
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

