package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Main(
    val temp: Double = 0.0,
    val tempMin: Double = 0.0,
    val humidity: Int = 0,
    val pressure: Double = 0.0,
    val tempMax: Double = 0.0
) : Parcelable