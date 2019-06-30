package xyz.razzaq.androidarchitecture.data.source.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PostDao {
    @Query("select * from databasepost")
    fun getPosts(): LiveData<List<DatabasePost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPosts(vararg posts: DatabasePost)

    @Query("select * from databasecomment where postId = :postId")
    fun getCommentsByPostId(postId: Int): LiveData<List<DatabaseComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllComments(vararg comments: DatabaseComment)
}

@Database(entities = [DatabasePost::class, DatabaseComment::class], version = 322, exportSchema = false)
abstract class PostsDatabase : RoomDatabase() {
    abstract val postDao: PostDao
}

private lateinit var INSTANCE: PostsDatabase

fun getDatabase(context: Context): PostsDatabase {
    synchronized(PostsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                PostsDatabase::class.java,
                "db_post"
            ).build()
        }
    }
    return INSTANCE
}