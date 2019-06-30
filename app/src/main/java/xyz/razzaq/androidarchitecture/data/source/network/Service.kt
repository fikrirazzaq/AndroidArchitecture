package xyz.razzaq.androidarchitecture.data.source.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import xyz.razzaq.androidarchitecture.domain.PostBody


interface PostsService {
    @GET("posts")
    fun getAllPostsAsync(): Deferred<List<NetworkPost>>

    @POST("posts")
    fun addPostAsync(@Body body: PostBody): Deferred<Response<NetworkPost>>

    @GET("comments")
    fun getCommentsByPostIdAsync(@Query("postId") postId: String): Deferred<List<NetworkComment>>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val posts: PostsService = retrofit.create(PostsService::class.java)
}