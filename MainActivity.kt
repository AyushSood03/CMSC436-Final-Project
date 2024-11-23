package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var settingsButton : Button
    private lateinit var trendingSpace1 : TextView
    private lateinit var trendingSpace2 : TextView
    private lateinit var customSpace1 : TextView
    private lateinit var customSpace2 : TextView
    private lateinit var customSpace3 : TextView
    private lateinit var customSpace4 : TextView

    private var isMetric : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        getPreferences()

        trendingSpace1 = findViewById(R.id.trending_space1)
        trendingSpace2 = findViewById(R.id.trending_space2)
        customSpace1 = findViewById(R.id.custom_space1)
        customSpace2 = findViewById(R.id.custom_space2)
        customSpace3 = findViewById(R.id.custom_space3)
        customSpace4 = findViewById(R.id.custom_space4)


        settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {toSettings()}
    }

    fun toSettings() {
        var intent : Intent = Intent(this, SettingsActivity::class.java)
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
    }
}