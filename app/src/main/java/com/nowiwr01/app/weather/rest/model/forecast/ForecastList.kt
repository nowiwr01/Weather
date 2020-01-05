package com.nowiwr01.app.weather.rest.model.forecast

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForecastList(
    @SerializedName("list")
    val forecast: List<ForecastWeatherModel>,
    @SerializedName("city")
    val city: City
): Parcelable