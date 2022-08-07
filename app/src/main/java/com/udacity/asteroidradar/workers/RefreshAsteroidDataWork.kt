package com.udacity.asteroidradar.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.db.AsteroidRoomDatabase
import com.udacity.asteroidradar.repository.AsteroidRepositoryImpl
import retrofit2.HttpException

class RefreshAsteroidDataWork(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val database = AsteroidRoomDatabase.getDatabase(context = applicationContext)
        val repository = AsteroidRepositoryImpl(database = database)

        return try {
            repository.getAsteroidList()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}