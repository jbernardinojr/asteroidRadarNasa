package com.udacity.asteroidradar.db

import android.content.Context
import androidx.room.*
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.Asteroid
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroid WHERE closeApproachDate >= :startDate AND closeApproachDate <= :endDate ORDER BY closeApproachDate ASC")
    fun getAsteroidsByCloseApproachDate(startDate: String, endDate: String): Flow<List<Asteroid>>

    @Query("SELECT * FROM asteroid ORDER BY closeApproachDate ASC")
    fun getAllAsteroids(): Flow<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidDatabase)

    @Query("DELETE FROM asteroid WHERE closeApproachDate < :today")
    fun deletePreviousDayAsteroids(today: String): Int
}

@Database(entities = [AsteroidDatabase::class], version = 1, exportSchema = false)
abstract class AsteroidRoomDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: AsteroidRoomDatabase

        fun getDatabase(context: Context): AsteroidRoomDatabase {
            synchronized(AsteroidDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidRoomDatabase::class.java,
                        Constants.DB_NAME
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}