package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

// API Key: 8706163ae20d4101be7163438242711

class MainActivity : AppCompatActivity() {
    private lateinit var settingsButton : Button
    private lateinit var searchButton : Button
    private var trendingSpaces : MutableList<TextView> = mutableListOf<TextView>()
    private var customSpaces : MutableList<TextView> = mutableListOf<TextView>()

    private var favLon1 : String = ""
    private var favLon2 : String = ""
    private var favLat1 : String = ""
    private var favLat2 : String = ""

    private var trendLon1 : String = ""
    private var trendLon2 : String = ""
    private var trendLat1 : String = ""
    private var trendLat2 : String = ""

    private var isMetric : Boolean = false

    // I'm just tired at this point.
    private var isTrend1 : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        trendingSpaces.add(findViewById(R.id.trending_space1))
        trendingSpaces.add(findViewById(R.id.trending_space2))
        customSpaces.add(findViewById(R.id.custom_space1))
        customSpaces.add(findViewById(R.id.custom_space2))
//        customSpaces.add(findViewById(R.id.custom_space3))
//        customSpaces.add(findViewById(R.id.custom_space4))

        settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {toSettings()}
        searchButton = findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {toSearch()}

        getPreferences()

        // Ads.
        var adView : AdView = AdView(this)
        var adSize : AdSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)
        var adUnitId : String = "ca-app-pub-3940256099942544/6300978111"
        adView.adUnitId = adUnitId
        var builder : AdRequest.Builder = AdRequest.Builder()
        builder.addKeyword("weather")
        var request : AdRequest = builder.build()
        var adLayout : LinearLayout = findViewById(R.id.ad_view)
        adLayout.addView(adView)
        adView.loadAd(request)

        if (!favLon1.equals("none") && favLat1.equals("none")) {
            var task1 : HomeApiTask = HomeApiTask(this, favLon1, favLat1, isMetric, 0, false)
            task1.start()
        }
        if (!favLon2.equals("none") && favLat2.equals("none")) {
            var task2: HomeApiTask = HomeApiTask(this, favLon2, favLat2, isMetric, 1, false)
            task2.start()
        }
    }

    override fun onResume() {
        super.onResume()
        adView.resume()

        getPreferences()

        if (!favLon1.equals("none") && !favLat1.equals("none")) {
            var task1 : HomeApiTask = HomeApiTask(this, favLon1, favLat1, isMetric, 0, false)
            task1.start()
        }
        if (!favLon2.equals("none") && !favLat2.equals("none")) {
            var task2: HomeApiTask = HomeApiTask(this, favLon2, favLat2, isMetric, 1, false)
            task2.start()
        }
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    fun updateView(s : String, locNum : Int, topKey : String, secondTopKey : String) {
        customSpaces[locNum].text = s

        var geocoder : Geocoder = Geocoder(this)
        var handler : GeocodingHandler = GeocodingHandler()
        geocoder.getFromLocationName(topKey, 1, handler)
        Thread.sleep(500L)
        isTrend1 = false
        geocoder.getFromLocationName(secondTopKey, 1, handler)
        Thread.sleep(500L)

        var task1 : HomeApiTask = HomeApiTask(this, trendLon1, trendLat1, isMetric, 0, true)
        task1.start()
        var task2 : HomeApiTask = HomeApiTask(this, trendLon2, trendLat2, isMetric, 1, true)
        task2.start()
        isTrend1 = true
    }

    fun updateTrending(s : String, locNum : Int) {
        trendingSpaces[locNum].text = s
    }

    // There are some things that I cannot change using the day theme, so I am doing them
    // programmatically here.
    fun toDayColors() {
        // These change the colors of the information spaces to the day theme colors.
        for (space in trendingSpaces) {
            space.setBackgroundResource(R.drawable.rounded_corner_view)
        }
        for (space in customSpaces) {
            space.setBackgroundResource(R.drawable.rounded_corner_view)
        }

        // This changes the color of the settings and search buttons to a red-pink color. This part
        // alone took longer than I would like to admit - Leo.
        settingsButton.backgroundTintList = ColorStateList.valueOf(Color.rgb(240, 83, 88))
        searchButton.backgroundTintList = ColorStateList.valueOf(Color.rgb(240, 83, 88))
    }

    // There are some things that I cannot change using the night theme, so I am doing them
    // programmatically here.
    fun toNightColors() {
        // These change the colors of the information spaces to the night theme colors.
        for (space in trendingSpaces) {
            space.background = resources.getDrawable(R.drawable.rounded_corner_view_night)
        }
        for (space in customSpaces) {
            space.background = resources.getDrawable(R.drawable.rounded_corner_view_night)
        }

        // This changes the color of the settings and search buttons to white.
        settingsButton.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        searchButton.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
    }

    fun toSettings() {
        var intent : Intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
    }

    fun toSearch() {
        var intent : Intent = Intent(this, SearchActivity::class.java)
        this.startActivity(intent)
    }

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

        favLon1 = pref.getString("favorite_lon1".toString(), "none").toString()
        favLon2 = pref.getString("favorite_lon2".toString(), "none").toString()
        favLat1 = pref.getString("favorite_lat1".toString(), "none").toString()
        favLat2 = pref.getString("favorite_lat2".toString(), "none").toString()
    }

    inner class GeocodingHandler : Geocoder.GeocodeListener {
        override fun onGeocode(p0: MutableList<Address>) {
            if (p0.size >= 1) {
                if (isTrend1) {
                    trendLon1 = p0[0].longitude.toString()
                    trendLat1 = p0[0].latitude.toString()
                } else {
                    trendLon2 = p0[0].longitude.toString()
                    trendLat2 = p0[0].latitude.toString()
                }
            } else {
                Log.w("MainActivity", "How did this even happen!?")
            }
        }

        override fun onError(errorMessage: String?) {
            super.onError(errorMessage)
            Log.w("MainActivity", "Error: " + errorMessage)
        }
    }
}