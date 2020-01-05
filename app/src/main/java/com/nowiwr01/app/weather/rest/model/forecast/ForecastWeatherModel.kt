package com.nowiwr01.app.weather.rest.model.forecast

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForecastWeatherModel(
    @Ignore val sys: Sys,
    @Ignore val clouds: Clouds,
    @Ignore @SerializedName("dt_txt") val dtTxt: String = "",
    val dt: Long = 0,
    val weather: List<WeatherItem>,
    val main: Main,
    val wind: Wind
): Parcelable