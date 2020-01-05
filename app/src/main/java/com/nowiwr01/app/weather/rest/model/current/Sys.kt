package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sys(
    val country: String = "",
    val sunrise: Int = 0,
    val sunset: Int = 0,
    val id: Int = 0,
    val type: Int = 0,
    val message: Double = 0.0
) : Parcelable