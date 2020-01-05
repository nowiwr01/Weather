package com.nowiwr01.app.weather.rest.model.forecast

import android.os.Parcelable
import androidx.room.Ignore
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wind(
    @Ignore val deg: Double = 0.0,
    val speed: Double = 0.0
): Parcelable