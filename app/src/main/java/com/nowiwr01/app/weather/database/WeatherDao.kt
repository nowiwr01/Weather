package com.nowiwr01.app.weather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nowiwr01.app.weather.adapters.model.DailyForecast
import com.nowiwr01.app.weather.adapters.model.HourlyForecast
import com.nowiwr01.app.weather.rest.model.current.CurrentWeatherModelEntity

@Dao
interface WeatherDao {
    @Insert
    fun insertWeather(weather: CurrentWeatherModelEntity)

    @Insert
    fun insertForecast(forecast: DailyForecast)

    @Insert
    fun insertHours(hours: HourlyForecast)

    @Query("SELECT * FROM weather ORDER BY ID DESC LIMIT 1")
    fun fetchWeather(): CurrentWeatherModelEntity

    @Query("SELECT * FROM forecast")
    fun fetchForecast(): List<DailyForecast>

    @Query("SELECT * FROM hours WHERE day = :day ORDER BY ID DESC LIMIT 5")
    fun fetchHours(day: String): List<HourlyForecast>

    @Query("DELETE FROM weather")
    fun deleteWeather()

    @Query("DELETE FROM forecast")
    fun deleteForecast()

    @Query("DELETE FROM hours")
    fun deleteHours()
}