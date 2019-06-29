package xyz.razzaq.androidarchitecture.data.source.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkPost(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)