package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wind(
    val deg: Double = 0.0,
    val speed: Double = 0.0
) : Parcelable