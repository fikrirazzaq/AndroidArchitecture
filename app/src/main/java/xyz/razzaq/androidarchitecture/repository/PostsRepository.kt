package xyz.razzaq.androidarchitecture.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import xyz.razzaq.androidarchitecture.data.source.database.*
import xyz.razzaq.androidarchitecture.data.source.network.Network
import xyz.razzaq.androidarchitecture.domain.Comment
import xyz.razzaq.androidarchitecture.domain.Post
import xyz.razzaq.androidarchitecture.domain.PostBody

class PostsRepository(private val database: PostsDatabase) {

    // All Posts
    val posts: LiveData<List<Post>> =
        Transformations.map(database.postDao.getPosts()) {
            it.asDomainPostModel()
        }

    suspend fun refreshPosts() {
        withContext(Dispatchers.IO) {
            try {
                val allPost = Network.posts.getAllPostsAsync().await()
                database.postDao.insertAllPosts(*allPost.map {
                    DatabasePost(
                        id = it.id,
                        title = it.title,
                        body = it.body,
                        userId = it.userId
                    )
                }.toTypedArray())
            } catch (exception: Exception) {
                Timber.e("Error ${exception.localizedMessage}")
            }
        }
    }

    // Create Post
    var responseCode: String? = null

    suspend fun addPost(title: String, body: String, userId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val addPost = Network.posts.addPostAsync(PostBody(title, body, userId)).await()
                responseCode = addPost.code().toString()
            } catch (exception: Exception) {
                Timber.e("Error ${exception.localizedMessage}")
                responseCode = "400"
            }
        }
    }

    // All Comments by postId

    fun comments(postId: Int): LiveData<List<Comment>> {
        return Transformations.map(database.postDao.getCommentsByPostId(postId)) {
            it.asDomainCommentModel()
        }
    }

    suspend fun getCommentsByPostId(postId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val comments = Network.posts.getCommentsByPostIdAsync(postId.toString()).await()
                database.postDao.insertAllComments(*comments.map {
                    DatabaseComment(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        body = it.body,
                        postId = it.postId
                    )
                }.toTypedArray())
            } catch (exception: Exception) {
                Timber.e("Error ${exception.localizedMessage}")
            }
        }
    }
}