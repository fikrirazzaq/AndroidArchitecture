package xyz.razzaq.androidarchitecture.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.razzaq.androidarchitecture.data.source.database.getDatabase
import xyz.razzaq.androidarchitecture.repository.PostsRepository

class PostsViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val postsRepository = PostsRepository(database)

    init {
        viewModelScope.launch {
            postsRepository.refreshPosts()
        }
    }

    val allPost = postsRepository.posts

    fun addPost(title: String, body: String, userId: Int) {
        viewModelScope.launch {
            val post = postsRepository.addPost(title, body, userId)
            if (post.isSuccessful) {
                Timber.d("Success ${post.body()!!.title.toString()}")
            } else {
                Timber.d("Error ${post.code()}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            with(modelClass) {
                when {
                    isAssignableFrom(PostsViewModel::class.java) -> PostsViewModel(app)
                    else ->
                        error("Invalid View Model class")
                }
            } as T
    }
}