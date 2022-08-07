package com.udacity.asteroidradar.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Utils {
    companion object DateUtils {
        fun getTodayFormatted(): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_PATTERN)

            return current.format(formatter)
        }

        fun getYesterdayFormatted(): String {
            val current = LocalDateTime.now().minusDays(DAYS_TO_MINUS)
            val formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_PATTERN)

            return current.format(formatter)

        }

        fun getSevenDaysAheadFormatted(): String {
            val current = LocalDateTime.now().plusDays(DAYS_TO_ADD)
            val formatter = DateTimeFormatter.ofPattern(FORMAT_DATE_PATTERN)

            return current.format(formatter)
        }

        private const val DAYS_TO_ADD = 7L
        private const val DAYS_TO_MINUS = 1L
        private const val FORMAT_DATE_PATTERN = "yyyy-MM-dd"
    }


}