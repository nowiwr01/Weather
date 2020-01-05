package com.nowiwr01.app.weather.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nowiwr01.app.weather.MainActivity.Companion.KEY
import com.nowiwr01.app.weather.MainActivity.Companion.isChanged
import com.nowiwr01.app.weather.MainActivity.Companion.preferences
import com.nowiwr01.app.weather.R
import com.nowiwr01.app.weather.adapters.BaseAdapterCallback
import com.nowiwr01.app.weather.adapters.DailyForecastAdapter
import com.nowiwr01.app.weather.adapters.model.DailyForecast
import com.nowiwr01.app.weather.presenters.HomePresenter
import com.nowiwr01.app.weather.presenters.WeatherContract
import com.nowiwr01.app.weather.rest.model.current.CurrentWeatherModel
import com.nowiwr01.app.weather.rest.model.current.CurrentWeatherModelEntity
import com.nowiwr01.app.weather.rest.model.forecast.ForecastWeatherModel
import com.nowiwr01.app.weather.service.DatabaseIntentService
import com.nowiwr01.app.weather.utils.Utils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_progress.*
import java.util.*

@ExperimentalStdlibApi
class HomeFragment(override var days: ArrayList<DailyForecast>,
                   override var weather: CurrentWeatherModelEntity?
) : BaseFragment(), WeatherContract.HomeContract {

    companion object {
        const val TYPE_ADD_WEATHER = 0
        const val TYPE_ADD_FORECAST = 1
        const val TYPE_FETCH_WEATHER = 2
        const val TYPE_FETCH_FORECAST = 3
        const val TYPE_ADD_HOURS = 4
        const val TYPE_FETCH_HOURS = 5

        fun newInstance(): HomeFragment {
            return HomeFragment(days = arrayListOf(), weather = null)
        }
    }

    private var currentCity: String = ""
    private var timezoneOffset: Long = 0

    private lateinit var views: List<View>
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: HomePresenter
    private lateinit var viewAdapter: DailyForecastAdapter

    private lateinit var weatherReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = if (isChanged) {
            activity!!.window.statusBarColor = resources.getColor(R.color.NightBackgroundColor)
            ContextThemeWrapper(activity, R.style.DarkTheme)
        } else {
            activity!!.window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
            ContextThemeWrapper(activity, R.style.BaseTheme)
        }
        return inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.fragment_home, container, false)
    }

    override fun onRefresh() {
        super.onRefresh()
        days.clear()
        viewAdapter.clearItems()
        viewAdapter.notifyDataSetChanged()
        presenter.loadWeatherByCity("Los Angeles")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        days = arrayListOf()

        toolbar.inflateMenu(R.menu.menu)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> doSearch(menuItem)
                R.id.changeTheme -> changeTheme()
                else -> false
            }
        }

        presenter = HomePresenter()
        presenter.attach(this)

        viewAdapter = DailyForecastAdapter()
        viewAdapter.attachCallback(object: BaseAdapterCallback<DailyForecast> {
            override fun onItemClick(model: DailyForecast, view: View) {
                val currentFragment = activity!!.supportFragmentManager.fragments.last()
                activity!!.supportFragmentManager.beginTransaction()
                    .hide(currentFragment)
                    .add(R.id.container, HourlyForecastFragment.newInstance(model.day, currentCity), null)
                    .addToBackStack(null)
                    .commit()
            }
        })

        recyclerView = dailyForecastList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            isNestedScrollingEnabled = false
            adapter = viewAdapter
        }

        views = listOf<View>(
            temperature, weather_condition_text_view, humadity, wind, drop, flag
        )

        presenter.loadWeatherByCity("Singapore")

        setReceiver()
    }

    private fun changeTheme(): Boolean {
        isChanged = !isChanged
        preferences.edit().putBoolean(KEY, isChanged).apply()
        fragmentManager!!
            .beginTransaction()
            .detach(this)
            .attach(this)
            .commit()
        return true
    }

    private fun doSearch(item: MenuItem): Boolean {
        val searchView = item.actionView as SearchView
        searchView.queryHint = "Type here to Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(curRequest: String?): Boolean {
                days.clear()
                viewAdapter.clearItems()
                viewAdapter.notifyDataSetChanged()
                presenter.loadWeatherByCity(curRequest ?: "Moscow")
                item.collapseActionView()
                return false
            }
            override fun onQueryTextChange(prefix: String?) = false
        })
        return true
    }

    private fun setVisibility(visibility: Int) {
        for (view in views) {
            view.visibility = visibility
        }
    }

    override fun showProgress() {
        setVisibility(View.GONE)
        progress.visibility = View.GONE
    }

    override fun hideProgress() {
        setVisibility(View.VISIBLE)
        progress.visibility = View.GONE
    }

    override fun showError() {
        progress.visibility = View.GONE
        Snackbar.make(coordinator_layout, "No internet connection", Snackbar.LENGTH_LONG).show()
    }

    override fun addForecastToDatabase() {
        val intent = Intent().apply {
            setClass(context!!, DatabaseIntentService::class.java)
            putExtra("forecast", days)
            putExtra("type", TYPE_ADD_FORECAST)
        }
        context!!.startService(intent)
    }

    override fun fetchWeather() {
        val intent = Intent().apply {
            setClass(context!!, DatabaseIntentService::class.java)
            putExtra("type", TYPE_FETCH_WEATHER)
        }
        context!!.startService(intent)
    }

    override fun fetchForecast() {
        val intent = Intent().apply {
            setClass(context!!, DatabaseIntentService::class.java)
            putExtra("type", TYPE_FETCH_FORECAST)
        }
        context!!.startService(intent)
    }

    override fun addWeatherToDatabase() {
        val intent = Intent().apply {
            setClass(context!!, DatabaseIntentService::class.java)
            putExtra("weather", weather)
            putExtra("type", TYPE_ADD_WEATHER)
        }
        context!!.startService(intent)
    }

    private fun setReceiver() {
        weatherReceiver = WeatherReceiver()
        val intentFilter = IntentFilter().apply {
            addAction("database")
        }
        LocalBroadcastManager.getInstance(context!!).registerReceiver(weatherReceiver, intentFilter)
    }

    private inner class WeatherReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getIntExtra("type", -1)) {
                TYPE_FETCH_WEATHER -> {
                    justUpdateWeather(intent.getParcelableExtra("get_weather"))
                }
                TYPE_FETCH_FORECAST -> {
                    (intent.getParcelableArrayListExtra<DailyForecast>("get_forecast")!!).forEach {
                        viewAdapter.addItem(it)
                    }
                }
            }
        }
    }

    fun justUpdateWeather(modelCurrent: CurrentWeatherModelEntity?) {
        if (modelCurrent != null) {
            city.text = modelCurrent.cityName
            wind.text = modelCurrent.windSpeed
            icon.setImageResource(modelCurrent.iconName)
            temperature.text = modelCurrent.temperature
            weather_condition_text_view.text = modelCurrent.weatherCondition
            humadity.text = modelCurrent.humadity
            timezoneOffset = modelCurrent.timezoneOffset
            currentCity = modelCurrent.currentCity
        }
    }

    override fun updateWeather(modelCurrent: CurrentWeatherModel) {
        val cityText = "${modelCurrent.name}, ${modelCurrent.sys.country}"
        val windText = "${modelCurrent.wind.speed} м/c"
        val iconName = Utils.getIconNameResource(modelCurrent.weather[0].icon)
        val temperatureText = Utils.formatTemperature(modelCurrent.main.temp)
        val weatherCondition = modelCurrent.weather[0].description.capitalize(Locale.getDefault())
        val humadityText = "${modelCurrent.main.humidity} %"
        val timezoneOffsetText = modelCurrent.timezone
        val currentCityText = modelCurrent.name

        city.text = cityText
        wind.text = windText
        icon.setImageResource(iconName)
        temperature.text = temperatureText
        weather_condition_text_view.text = weatherCondition
        humadity.text = humadityText
        timezoneOffset = timezoneOffsetText
        currentCity = currentCityText

        weather = CurrentWeatherModelEntity(
            0,
            cityText,
            windText,
            iconName,
            temperatureText,
            weatherCondition,
            humadityText,
            timezoneOffsetText,
            currentCityText
        )
    }

    override fun addDay(modelForecast: ForecastWeatherModel) {
        val dt = modelForecast.dt + timezoneOffset

        val dayForecast = DailyForecast(
            0,
            Utils.getIconNameResource(modelForecast.weather[0].icon),
            Utils.getDateTime(dt, "dd"),
            Utils.getDateTime(dt, "E"),
            "${modelForecast.wind.speed.toInt()} м/c",
            modelForecast.weather[0].description,
            Utils.formatTemperature(modelForecast.main.temp)
        )
        days.add(dayForecast)
        viewAdapter.addItem(dayForecast)
    }
}