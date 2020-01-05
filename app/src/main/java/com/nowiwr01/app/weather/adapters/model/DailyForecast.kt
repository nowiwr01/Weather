package com.nowiwr01.app.weather.adapters.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "forecast")
data class DailyForecast(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val icon: Int,
    val day: String,
    val date: String,
    val wind: String,
    val description: String,
    val temperature: String
): Parcelable