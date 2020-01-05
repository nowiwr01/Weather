package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "weather")
data class CurrentWeatherModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val cityName: String,
    val windSpeed: String,
    val iconName: Int,
    val temperature: String,
    val weatherCondition: String,
    val humadity: String,
    val timezoneOffset: Long,
    val currentCity: String
) : Parcelable