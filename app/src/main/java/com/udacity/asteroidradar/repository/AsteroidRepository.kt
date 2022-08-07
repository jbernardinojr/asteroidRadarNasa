package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.db.AsteroidRoomDatabase
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.domain.asDomainModel
import com.udacity.asteroidradar.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

interface AsteroidRepository {
    suspend fun getAsteroidList()
    suspend fun getPictureOfDay(): PictureOfDay
    suspend fun deletePreviousAsteroidData()
}

class AsteroidRepositoryImpl(private val database: AsteroidRoomDatabase) : AsteroidRepository {
    override suspend fun getAsteroidList(
    ) {
        withContext(Dispatchers.IO) {
            val asteroidResponse = Network.service.getAsteroidList(
                startDate = Utils.getTodayFormatted(),
                endDate = Utils.getSevenDaysAheadFormatted(),
                Constants.API_KEY
            ).await()

            val asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponse.string()))
            database.asteroidDao.insertAll(*asteroidList.asDomainModel())
        }
    }

    override suspend fun getPictureOfDay(): PictureOfDay {
        val pictureOfDay = withContext(Dispatchers.IO) {
            Network.service.getPictureOfDay(
                Constants.API_KEY
            ).await()
        }

        return pictureOfDay
    }

    override suspend fun deletePreviousAsteroidData() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deletePreviousDayAsteroids(today = Utils.getYesterdayFormatted())
        }
    }
}