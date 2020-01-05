package com.nowiwr01.app.weather.presenters

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.nowiwr01.app.weather.utils.ApiUtils
import com.nowiwr01.app.weather.utils.Utils
import com.nowiwr01.app.weather.fragments.HourlyForecastFragment

@ExperimentalStdlibApi
class HourlyForecastPresenter : BasePresenter<HourlyForecastFragment>() {

    fun loadForecastForDayByCity(day: String, city: String) {

        view.showProgress()

        subscribe(ApiUtils.weatherApi.getForecastByCity(city)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                view.setTimezone(it.city.timezone)
            }
            .flatMap { forecastList ->
                Observable.fromIterable(forecastList.forecast).filter {
                    day.startsWith(Utils.getDateTime(it.dt + forecastList.city.timezone, "dd"))
                }
            }
            .doOnComplete {
                view.hideProgress()
                view.addHoursToDatabase()
            }
            .subscribe({
//                Log.d("Hours", "Hours added: ${it.}")
                view.addHour(it)
            }, {
                view.showError()
                view.showProgress()
                view.fetchHoursFromDatabase()
                view.hideProgress()
            })
        )

    }
}