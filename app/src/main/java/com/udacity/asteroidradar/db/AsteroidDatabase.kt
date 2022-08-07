package com.udacity.asteroidradar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TABLE_ASTEROID)
data class AsteroidDatabase constructor(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

private const val TABLE_ASTEROID = "asteroid"