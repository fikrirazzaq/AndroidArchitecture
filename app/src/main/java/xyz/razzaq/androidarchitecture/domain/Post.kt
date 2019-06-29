package xyz.razzaq.androidarchitecture.domain

data class Post(val id: Int,
                val title: String,
                val body: String,
                val userId: Int)

data class PostBody(val title: String, val body: String, val userId: Int)