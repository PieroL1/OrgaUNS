package com.example.orgauns.domain.model

data class Note(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

