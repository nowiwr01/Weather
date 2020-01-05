package com.nowiwr01.app.weather

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import kotlinx.android.synthetic.main.fragment_home.*
import com.nowiwr01.app.weather.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = applicationContext.getSharedPreferences("preferences", MODE_PRIVATE)
        isChanged = preferences.getBoolean(KEY, false)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (supportFragmentManager.backStackEntryCount < 1) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, HomeFragment.newInstance(), null)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY, isChanged)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isChanged = savedInstanceState.getBoolean(KEY)
    }

    companion object {
        var isChanged = false
        lateinit var preferences: SharedPreferences
        const val KEY = "isChanged"
    }
}
