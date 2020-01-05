package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Coord(val lon: Double = 0.0, val lat: Double = 0.0) : Parcelable