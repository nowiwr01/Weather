package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherItem(
    val icon: String = "",
    val description: String = "",
    val main: String = "",
    val id: Int = 0
) : Parcelable