package com.nowiwr01.app.weather.adapters.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "hours")
data class HourlyForecast(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val day: String,
    val icon: Int,
    val datetime: String,
    val description: String,
    val lowTemp: String,
    val highTemp: String
): Parcelable