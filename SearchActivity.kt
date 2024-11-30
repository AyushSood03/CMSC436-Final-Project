package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SearchActivity : AppCompatActivity() {
    private lateinit var homeButton : Button
    private lateinit var searchButton : Button
    private lateinit var searchBar : EditText

    // Yes, there is a mutable list of mutable lists. There has to be two per entry in weatherTexts.
    private var weatherTexts : MutableList<TextView> = mutableListOf<TextView>()
    private var favButtons : MutableList<MutableList<Button>> = mutableListOf<MutableList<Button>>()

    private var isMetric : Boolean = false
    private var lon : MutableList<String> = mutableListOf<String>()
    private var lat : MutableList<String> = mutableListOf<String>()

    // You can tell how much it hurt me to create this based on its name.
    private var shittyDelayThing : Boolean = true

    // It's getting late and I'm getting sloppy.
    private var numLocs : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        homeButton = findViewById(R.id.home_button)
        searchButton = findViewById(R.id.search_button)
        searchBar = findViewById(R.id.search_bar)

        weatherTexts.add(findViewById(R.id.weatherText0))
        weatherTexts.add(findViewById(R.id.weatherText1))
        weatherTexts.add(findViewById(R.id.weatherText2))
        weatherTexts.add(findViewById(R.id.weatherText3))
        weatherTexts.add(findViewById(R.id.weatherText4))

        val buttonList0 : MutableList<Button> = mutableListOf(findViewById(R.id.fav_button0a), findViewById(R.id.fav_button0b))
        favButtons.add(buttonList0)
        val buttonList1 : MutableList<Button> = mutableListOf(findViewById(R.id.fav_button1a), findViewById(R.id.fav_button1b))
        favButtons.add(buttonList1)
        val buttonList2 : MutableList<Button> = mutableListOf(findViewById(R.id.fav_button2a), findViewById(R.id.fav_button2b))
        favButtons.add(buttonList2)
        val buttonList3 : MutableList<Button> = mutableListOf(findViewById(R.id.fav_button3a), findViewById(R.id.fav_button3b))
        favButtons.add(buttonList3)
        val buttonList4 : MutableList<Button> = mutableListOf(findViewById(R.id.fav_button4a), findViewById(R.id.fav_button4b))
        favButtons.add(buttonList4)

        getPreferences()

        homeButton.setOnClickListener {goBack()}
        searchButton.setOnClickListener {geocode(searchBar.text.toString())}

        // This also gives onClickListeners to the favButtons so they can favorite things. For some
        // reason, making x a counter and incrementing it didn't work, but this did - Leo.
        var arr : Array<Int> = arrayOf(0,1,2,3,4)
        for (x in arr) {
            favButtons[x][0].setOnClickListener {favorite1(x)}
            favButtons[x][1].setOnClickListener {favorite2(x)}
            lon.add("none")
            lat.add("none")
        }
    }

    fun favorite1(locNum : Int) {
        var pref : SharedPreferences =
            this.getSharedPreferences(this.packageName+ "_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()

        // I could have done a putStringSet but I have no idea how to manipulate those, but this
        // will work too.
        editor.putString(PREFERENCE_FAV_LON1, lon[locNum])
        editor.putString(PREFERENCE_FAV_LAT1, lat[locNum])
        editor.commit()
    }

    fun favorite2(locNum : Int) {
        var pref : SharedPreferences =
            this.getSharedPreferences(this.packageName+ "_preferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = pref.edit()

        // I could have done a putStringSet but I have no idea how to manipulate those, but this
        // will work too.
        editor.putString(PREFERENCE_FAV_LON2, lon[locNum])
        editor.putString(PREFERENCE_FAV_LAT2, lat[locNum])
        editor.commit()
    }

    fun geocode(address : String) {
        var geocoder : Geocoder = Geocoder(this)
        var handler : GeocodingHandler = GeocodingHandler()
        geocoder.getFromLocationName(address, 5, handler)

        // Please forgive me for what I have done. I need this here to create a delay so that lon
        // and lat are updated in main class before the rest of the geocode function executes.
        while (shittyDelayThing) { /* Literally nothing happens in here. */ }

        shittyDelayThing = true

        var x : Int = 0
        while (x < numLocs) {
            var task : ApiTask = ApiTask(this, lon[x], lat[x], isMetric, x)
            task.start()
            x += 1
        }
    }

    fun updateView(s : String, locNum : Int) {
        weatherTexts[locNum].text = s
    }

    // Returns to the home page.
    fun goBack() {
        this.finish()
    }

    // There are some things that I cannot change using the day theme, so I am doing them
    // programmatically here.
    fun toDayColors() {
        searchBar.setBackgroundResource(R.drawable.rounded_corner_view)
        searchButton.backgroundTintList = ColorStateList.valueOf(Color.rgb(240, 83, 88))
    }

    // There are some things that I cannot change using the night theme, so I am doing them
    // programmatically here.
    fun toNightColors() {
        searchBar.setBackgroundResource(R.drawable.rounded_corner_view_night)
        searchButton.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
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

            // This part only activates if there is at least one result. It then updates the lon and
            // lat variables according to the longitude and latitude for the given location.
            if (p0.size >= 1) {
                numLocs = 0
                for (address in p0) {
                    lon[numLocs] = address.longitude.toString()
                    lat[numLocs] = address.latitude.toString()
                    numLocs += 1
                }

                shittyDelayThing = false

            } else {
                Log.w("MainActivity", "No location found with that name.")
                shittyDelayThing = false
            }
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            Log.w("MainActivity", "Error: " + errorMessage)
            shittyDelayThing = false
        }
    }

    companion object {
        private const val PREFERENCE_FAV_LON1 = "favorite_lon1"
        private const val PREFERENCE_FAV_LON2 = "favorite_lon2"
        private const val PREFERENCE_FAV_LAT1 = "favorite_lat1"
        private const val PREFERENCE_FAV_LAT2 = "favorite_lat2"
    }
}