package com.nowiwr01.app.weather.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.nowiwr01.app.weather.MainActivity
import kotlinx.android.synthetic.main.fragment_hourly_forecast.*
import kotlinx.android.synthetic.main.item_progress.*
import com.nowiwr01.app.weather.R
import com.nowiwr01.app.weather.utils.Utils
import com.nowiwr01.app.weather.adapters.BaseAdapter
import com.nowiwr01.app.weather.adapters.HourlyForecastAdapter
import com.nowiwr01.app.weather.adapters.model.HourlyForecast
import com.nowiwr01.app.weather.presenters.HourlyForecastPresenter
import com.nowiwr01.app.weather.presenters.WeatherContract
import com.nowiwr01.app.weather.rest.model.forecast.ForecastWeatherModel
import com.nowiwr01.app.weather.service.DatabaseIntentService
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalStdlibApi
class HourlyForecastFragment(override var hours: ArrayList<HourlyForecast>)
    : BaseListFragment<HourlyForecast>(), WeatherContract.HourlyForecastContract {

    private var timezoneOffset: Long = 0

    private lateinit var presenter: HourlyForecastPresenter
    private lateinit var weatherReceiver: BroadcastReceiver

    private val day: String
        get() = arguments!!.getString(DAY, "")

    private val city: String
        get() = arguments!!.getString(CITY, "")

    companion object {
        const val DAY = "day"
        const val CITY = "city"

        fun newInstance(day: String, city: String): HourlyForecastFragment {
            val bundle = Bundle().apply {
                putString(DAY, day)
                putString(CITY, city)
            }
            return HourlyForecastFragment(arrayListOf()).apply { arguments = bundle }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = if (MainActivity.isChanged) {
            activity!!.window.statusBarColor = resources.getColor(R.color.NightBackgroundColor)
            ContextThemeWrapper(activity, R.style.DarkTheme)
        } else {
            activity!!.window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
            ContextThemeWrapper(activity, R.style.BaseTheme)
        }
        return inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.fragment_hourly_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = HourlyForecastPresenter()
        presenter.attach(this)
        presenter.loadForecastForDayByCity(day, city)

        setReceiver()
    }

    private fun setReceiver() {
        weatherReceiver = WeatherReceiver()
        val intentFilter = IntentFilter().apply {
            addAction("database")
        }
        LocalBroadcastManager.getInstance(context!!).registerReceiver(weatherReceiver, intentFilter)
    }

    override fun onRefresh() {
        super.onRefresh()
        viewAdapter.clearItems()
        viewAdapter.notifyDataSetChanged()
        presenter.loadForecastForDayByCity(day, city)
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun showError() {
        progress.visibility = View.GONE
        Snackbar.make(swipeRefresh, "No internet connection", Snackbar.LENGTH_LONG).show()

    }

    override fun createAdapterInstance(): BaseAdapter<HourlyForecast> {
        return HourlyForecastAdapter()
    }

    override fun addHour(forecastWeatherModel: ForecastWeatherModel) {
        val dt = forecastWeatherModel.dt + timezoneOffset

        val hourlyForecast = HourlyForecast(
            0, day,
            Utils.getIconNameResource(forecastWeatherModel.weather[0].icon),
            Utils.getDateTime(dt, "EEEE, HH:mm"),
            "${forecastWeatherModel.weather[0].description.capitalize(Locale.getDefault())}, влажность ${forecastWeatherModel.main.humidity} %",
            Utils.formatTemperature(forecastWeatherModel.main.tempMin),
            Utils.formatTemperature(forecastWeatherModel.main.tempMax)
        )
        hours.add(hourlyForecast)
        viewAdapter.addItem(hourlyForecast)
    }

    override fun addHoursToDatabase() {
        val intent = Intent().apply {
            setClass(context!!, DatabaseIntentService::class.java)
            putExtra("hours", hours)
            putExtra("type", HomeFragment.TYPE_ADD_HOURS)
        }
        context!!.startService(intent)
    }

    override fun fetchHoursFromDatabase() {
        val intent = Intent().apply {
            setClass(context!!, DatabaseIntentService::class.java)
            putExtra("day", day)
            putExtra("type", HomeFragment.TYPE_FETCH_HOURS)
        }
        context!!.startService(intent)
    }

    override fun setTimezone(timezone: Long) {
        timezoneOffset = timezone
    }

    private inner class WeatherReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra("type", -1)) {
                HomeFragment.TYPE_FETCH_HOURS -> {
                    (intent.getParcelableArrayListExtra<HourlyForecast>("get_hours")!!).forEach {
                        viewAdapter.addItem(it)
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        presenter.attach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detach()
    }
}