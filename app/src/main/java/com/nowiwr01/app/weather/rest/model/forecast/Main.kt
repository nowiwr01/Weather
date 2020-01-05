package com.nowiwr01.app.weather.rest.model.forecast

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Main(
    val temp: Double = 0.0,
    @SerializedName("temp_min") val tempMin: Double = 0.0,
    @Ignore @SerializedName("grnd_level") val grndLevel: Double = 0.0,
    @Ignore @SerializedName("temp_kf") val tempKf: Double = 0.0,
    val humidity: Int = 0,
    @Ignore val pressure: Double = 0.0,
    @Ignore @SerializedName("sea_level") val seaLevel: Double = 0.0,
    @SerializedName("temp_max") val tempMax: Double = 0.0
): Parcelable