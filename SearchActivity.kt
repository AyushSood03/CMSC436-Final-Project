package com.example.finalproject

import android.text.method.ScrollingMovementMethod
import android.widget.*
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class SearchActivity : AppCompatActivity() {
    private lateinit var homeButton : Button
    private lateinit var searchBar : EditText

    private lateinit var testText : TextView
    private lateinit var testButton : Button

    private var isMetric : Boolean = false
    private var lon : String = ""
    private var lat : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        homeButton = findViewById(R.id.home_button)
        searchBar = findViewById(R.id.search_bar)

        testText = findViewById(R.id.test_text)
        testButton = findViewById(R.id.test_button)

        getPreferences()
        var task : ApiTask = ApiTask(this, "-77", "38.8", isMetric)

        homeButton.setOnClickListener {goBack()}
        testButton.setOnClickListener {task.start()}
    }

    fun geocode(address : String) {
        var geocoder : Geocoder = Geocoder(this)
    }

    fun updateView(s : String) {
        testText.text = s
    }

    // Returns to the home page.
    fun goBack() {
        this.finish()
    }

    // There are some things that I cannot change using the day theme, so I am doing them
    // programmatically here.
    fun toDayColors() {
        searchBar.setBackgroundResource(R.drawable.rounded_corner_view)
    }

    // There are some things that I cannot change using the night theme, so I am doing them
    // programmatically here.
    fun toNightColors() {
        searchBar.setBackgroundResource(R.drawable.rounded_corner_view_night)
    }

    // Retrieves the user preferences and calls functions to adjust the color theming
    // and measurements accordingly.
    fun getPreferences() {
        var pref : SharedPreferences =
            this.getSharedPreferences(this.packageName+ "_preferences", Context.MODE_PRIVATE)
        var isDark : Boolean = false

        isDark = pref.getBoolean("dark", false)
        isMetric = pref.getBoolean("unit", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        if (isDark) toNightColors()
        else toDayColors()
    }

    inner class GeocodingHandler : Geocoder.GeocodeListener {
        override fun onGeocode(p0: MutableList<Address>) {

            if (p0.size >= 1) {
                var result : Address = p0.get(0)
                lon = result.longitude.toString()
                lat = result.latitude.toString()
            } else {
                Log.w("MainActivity", "No location found with that name.")
            }
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            Log.w("MainActivity", "Error: " + errorMessage)
        }
    }
}