package com.nowiwr01.app.weather.presenters

import com.nowiwr01.app.weather.adapters.model.DailyForecast
import com.nowiwr01.app.weather.adapters.model.HourlyForecast
import com.nowiwr01.app.weather.rest.model.current.CurrentWeatherModel
import com.nowiwr01.app.weather.rest.model.current.CurrentWeatherModelEntity
import com.nowiwr01.app.weather.rest.model.forecast.ForecastWeatherModel

interface WeatherContract {

    interface BaseContract {
        fun showProgress()
        fun hideProgress()
        fun showError()
    }

    interface HomeContract : BaseContract {
        var days: ArrayList<DailyForecast>
        var weather: CurrentWeatherModelEntity?

        fun addWeatherToDatabase()
        fun updateWeather(modelCurrent: CurrentWeatherModel)

        fun addDay(modelForecast: ForecastWeatherModel)
        fun addForecastToDatabase()

        fun fetchWeather()
        fun fetchForecast()
    }

    interface HourlyForecastContract: BaseContract {
        var hours: ArrayList<HourlyForecast>

        fun addHour(forecastWeatherModel: ForecastWeatherModel)
        fun setTimezone(timezone: Long)
        fun addHoursToDatabase()

        fun fetchHoursFromDatabase()
    }
}
