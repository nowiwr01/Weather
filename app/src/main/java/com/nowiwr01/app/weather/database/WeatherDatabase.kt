package com.nowiwr01.app.weather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nowiwr01.app.weather.adapters.model.DailyForecast
import com.nowiwr01.app.weather.adapters.model.HourlyForecast
import com.nowiwr01.app.weather.rest.model.current.CurrentWeatherModelEntity

@Database(
    version = 2,
    exportSchema = false,
    entities = [CurrentWeatherModelEntity::class, DailyForecast::class, HourlyForecast::class]
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao
}