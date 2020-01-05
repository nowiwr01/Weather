package com.nowiwr01.app.weather.rest.model.current

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Clouds(val all: Int = 0) : Parcelable