package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber
import java.util.concurrent.TimeUnit


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface AsteroidApi {
    @GET("planetary/apod")
    fun getPictureOfDay(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): Deferred<PictureOfDay>

    @GET("neo/rest/v1/feed")
    fun getAsteroidList(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = Constants.API_KEY,
    ): Deferred<ResponseBody>
}

object Network {

    private val client: OkHttpClient by lazy { newClient() }

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    class HttpLog : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Timber.d(message)
        }
    }

    private fun newClient() = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        writeTimeout(10, TimeUnit.SECONDS)
        addInterceptor(HttpLoggingInterceptor(HttpLog()).setLevel(HttpLoggingInterceptor.Level.BODY))
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val service: AsteroidApi = retrofit.create(AsteroidApi::class.java)
}