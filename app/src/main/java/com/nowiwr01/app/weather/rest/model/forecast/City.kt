package com.nowiwr01.app.weather.rest.model.forecast

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val id: Long = 0,
    val name: String = "",
    val coord: Coord,
    val country: String = "",
    val timezone: Long = 0
): Parcelable