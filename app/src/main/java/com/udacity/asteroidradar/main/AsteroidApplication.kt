package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.workers.DeleteAsteroidDataWork
import com.udacity.asteroidradar.workers.RefreshAsteroidDataWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AsteroidApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        setupWorkers()
    }

    private fun setupWorkers() {
        applicationScope.launch {
            setupWorkersConstraints()
        }
    }

    private fun setupWorkersConstraints() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        setupRefreshDataWork(constraints)
        setupDeleteWork(constraints)
    }

    private fun setupDeleteWork(constraints: Constraints) {
        val repeatingDeleteRequest = PeriodicWorkRequestBuilder<DeleteAsteroidDataWork>(
            WORKER_FREQUENCY_INTERVAL_DAYS,
            TimeUnit.DAYS
        ).setConstraints(constraints)
            .setInitialDelay(
                WORKER_FREQUENCY_INTERVAL_DAYS,
                TimeUnit.DAYS
            )
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            DELETE_ASTEROIDS_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingDeleteRequest
        )
    }

    private fun setupRefreshDataWork(constraints: Constraints) {
        val repeatingRefreshRequest = PeriodicWorkRequestBuilder<RefreshAsteroidDataWork>(
            WORKER_FREQUENCY_INTERVAL_DAYS,
            TimeUnit.DAYS
        ).setConstraints(constraints)
            .setInitialDelay(
                WORKER_FREQUENCY_INTERVAL_DAYS,
                TimeUnit.DAYS
            )
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            REFRESH_ASTEROIDS_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRefreshRequest
        )
    }

    companion object {
        private const val WORKER_FREQUENCY_INTERVAL_DAYS = 1L
        private const val REFRESH_ASTEROIDS_WORK_NAME = "refresh_asteroids"
        private const val DELETE_ASTEROIDS_WORK_NAME = "delete_asteroids"
    }
}