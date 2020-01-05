package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentWeatherModel(
    val dt: Long = 0,
    val cod: Int = 0,
    val coord: Coord,
    val visibility: Int = 0,
    val clouds: Clouds,
    val id: Int = 0,
    val base: String = "",
    val timezone: Long = 0,
    val weather: List<WeatherItem>,
    val name: String = "",
    val main: Main,
    val sys: Sys,
    val wind: Wind
): Parcelable