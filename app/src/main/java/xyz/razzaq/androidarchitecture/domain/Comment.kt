package xyz.razzaq.androidarchitecture.domain

data class Comment(
    val id: Int,
    val name: String,
    val email: String,
    val body: String,
    val postId: Int
)