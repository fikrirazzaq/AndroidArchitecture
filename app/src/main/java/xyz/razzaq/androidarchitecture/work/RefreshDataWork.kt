package xyz.razzaq.androidarchitecture.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException
import xyz.razzaq.androidarchitecture.data.source.database.getDatabase
import xyz.razzaq.androidarchitecture.repository.PostsRepository

class RefreshDataWork(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = PostsRepository(database)
        return try {
            repository.refreshPosts()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}