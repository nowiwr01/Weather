package com.nowiwr01.app.weather.service

import android.app.IntentService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.nowiwr01.app.weather.adapters.model.DailyForecast
import com.nowiwr01.app.weather.adapters.model.HourlyForecast
import com.nowiwr01.app.weather.database.WeatherDatabase
import com.nowiwr01.app.weather.fragments.HomeFragment.Companion.TYPE_ADD_WEATHER
import com.nowiwr01.app.weather.fragments.HomeFragment.Companion.TYPE_ADD_FORECAST
import com.nowiwr01.app.weather.fragments.HomeFragment.Companion.TYPE_ADD_HOURS
import com.nowiwr01.app.weather.fragments.HomeFragment.Companion.TYPE_FETCH_FORECAST
import com.nowiwr01.app.weather.fragments.HomeFragment.Companion.TYPE_FETCH_HOURS
import com.nowiwr01.app.weather.fragments.HomeFragment.Companion.TYPE_FETCH_WEATHER
import com.nowiwr01.app.weather.rest.model.current.CurrentWeatherModelEntity

@ExperimentalStdlibApi
class DatabaseIntentService : IntentService(DatabaseIntentService::class.simpleName) {

    private lateinit var weatherDatabase: WeatherDatabase

    override fun onCreate() {
        super.onCreate()
        weatherDatabase =
            Room.databaseBuilder(applicationContext, WeatherDatabase::class.java, "weather-db")
                .fallbackToDestructiveMigration()
                .build()
    }

    override fun onHandleIntent(intent: Intent?) {
        val response = Intent("database")
        val type = intent?.getIntExtra("type", -1)
        when (type) {
            TYPE_ADD_HOURS -> addHoursToDatabase(intent.getParcelableArrayListExtra("hours")!!)
            TYPE_ADD_WEATHER -> addWeatherToDatabase(intent.getParcelableExtra("weather")!!)
            TYPE_ADD_FORECAST -> addForecastToDatabase(intent.getParcelableArrayListExtra("forecast")!!)
            TYPE_FETCH_WEATHER -> response.putExtra("get_weather", fetchWeatherFromDatabase())
            TYPE_FETCH_FORECAST -> response.putExtra("get_forecast", fetchForecastFromDatabase())
            TYPE_FETCH_HOURS -> response.putParcelableArrayListExtra(
                "get_hours", fetchHoursFromDatabase(intent.getStringExtra("day")!!)
            )
        }
        response.putExtra("type", type)
        LocalBroadcastManager.getInstance(this).sendBroadcast(response)
    }

    private fun addWeatherToDatabase(weather: CurrentWeatherModelEntity) {
        weatherDatabase.weatherDao.apply {
            deleteHours()
            deleteWeather()
            insertWeather(weather)
        }
    }

    private fun addForecastToDatabase(forecastList: ArrayList<DailyForecast>) {
        weatherDatabase.weatherDao.deleteForecast()
        forecastList.forEach { forecast -> weatherDatabase.weatherDao.insertForecast(forecast) }
    }

    private fun addHoursToDatabase(hours: ArrayList<HourlyForecast>) {
        hours.forEach { hour-> weatherDatabase.weatherDao.insertHours(hour) }
    }

    private fun fetchWeatherFromDatabase(): CurrentWeatherModelEntity? =
        weatherDatabase.weatherDao.fetchWeather()

    private fun fetchForecastFromDatabase(): ArrayList<DailyForecast>? =
        weatherDatabase.weatherDao.fetchForecast() as ArrayList<DailyForecast>

    private fun fetchHoursFromDatabase(day: String): ArrayList<HourlyForecast>? =
        weatherDatabase.weatherDao.fetchHours(day).reversed() as ArrayList<HourlyForecast>

}