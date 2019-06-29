package xyz.razzaq.androidarchitecture.data.source.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.razzaq.androidarchitecture.domain.Post

@Entity
data class DatabasePost constructor(
    @PrimaryKey
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)

fun List<DatabasePost>.asDomainModel(): List<Post> {
    return map {
        Post(
            id = it.id,
            title = it.title,
            body = it.body,
            userId = it.userId
        )
    }
}