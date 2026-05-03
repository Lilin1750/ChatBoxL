package com.example.chatboxl.domain.model

data class Message(
    val content: String,
    val timestamp: String,
    val isSentByMe: Boolean,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val id: Int
)