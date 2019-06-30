package xyz.razzaq.androidarchitecture.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.razzaq.androidarchitecture.data.source.database.getDatabase
import xyz.razzaq.androidarchitecture.domain.Comment
import xyz.razzaq.androidarchitecture.repository.PostsRepository

class PostsViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val postsRepository = PostsRepository(database)

    var isLoaded = MutableLiveData<Boolean>()
    var resultMessage = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            postsRepository.refreshPosts()
        }
    }

    val allPost = postsRepository.posts

    fun addPost(title: String, body: String, userId: Int) {
        viewModelScope.launch {
            postsRepository.addPost(title, body, userId)
            if (postsRepository.responseCode != "400") {
                loadFinished()
                resultMessage(true)
                Timber.d("Success ${postsRepository.responseCode}")
            } else {
                loadFinished()
                resultMessage(false)
                Timber.d("Error ${postsRepository.responseCode}")
            }
        }
    }

    fun getComments(postId: Int): LiveData<List<Comment>> {
        viewModelScope.launch {
            postsRepository.getCommentsByPostId(postId)
        }

        return postsRepository.comments(postId)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun loadFinished() {
        isLoaded.value = true
    }

    private fun resultMessage(message: Boolean) {
        resultMessage.value = message
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