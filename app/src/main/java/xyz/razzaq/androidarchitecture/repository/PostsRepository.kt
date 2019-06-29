package xyz.razzaq.androidarchitecture.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import xyz.razzaq.androidarchitecture.data.source.database.DatabasePost
import xyz.razzaq.androidarchitecture.data.source.database.PostsDatabase
import xyz.razzaq.androidarchitecture.data.source.database.asDomainModel
import xyz.razzaq.androidarchitecture.data.source.network.Network
import xyz.razzaq.androidarchitecture.data.source.network.NetworkPost
import xyz.razzaq.androidarchitecture.domain.Post
import xyz.razzaq.androidarchitecture.domain.PostBody

class PostsRepository(private val database: PostsDatabase) {

    val posts: LiveData<List<Post>> =
        Transformations.map(database.postDao.getPosts()) {
            it.asDomainModel()
        }

    suspend fun refreshPosts() {
        withContext(Dispatchers.IO) {
            val allPost = Network.posts.getAllPostsAsync().await()
            database.postDao.insertAll(*allPost.map {
                DatabasePost(
                    id = it.id,
                    title = it.title,
                    body = it.body,
                    userId = it.userId
                )
            }.toTypedArray())
        }
    }

    suspend fun addPost(title: String, body: String, userId: Int): Response<NetworkPost> {
        return withContext(Dispatchers.IO) {
            return@withContext Network.posts.addPostAsync(PostBody(title, body, userId)).await()
        }
    }

}