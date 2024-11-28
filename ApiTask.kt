package com.example.finalproject

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ApiTask : Thread {
    private var results : String = "EMPTY STRING"
    private lateinit var activity : SearchActivity

    private var lon : String = "0.0"
    private var lat : String = "0.0"
    private var isMetric : String = "imperial"

    constructor(activity : SearchActivity, lon : String, lat : String, isMetric : Boolean) {
        this.activity = activity

        this.lon = lon
        this.lat = lat

        // This is used to change the output between metric and imperial.
        if (isMetric) this.isMetric = "metric"
        else this.isMetric = "imperial"
    }

    override fun run() {
        super.run()

        // API key is: ea7c59639bmshfe35825cbd1a3f4p13245djsn536e148d52f6
        try {
            // This URL is just here to test things. REMOVE IT AFTERWARDS!
            // I have no idea why most of this stuff is here, this was just supposed to be here
            // according to RapidAPI - Leo.
            val client = OkHttpClient()
            val request = Request.Builder().url(
                "https://weatherbit-v1-mashape.p.rapidapi.com/current?lon="+lon+"&lat="+lat+"&units="+isMetric+"&lang=en")
                .get()
                .addHeader("x-rapidapi-key", "ea7c59639bmshfe35825cbd1a3f4p13245djsn536e148d52f6")
                .addHeader("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com")
                .build()
            val response = client.newCall(request).execute()
            results = response.body()!!.string()

        } catch (e : Exception) {
            Log.w("MainActivity", "Exception: " + e.message)
            Log.w("MainActivity", "Exception: " + e.toString())
        }

        var updateGui : UpdateGui = UpdateGui()
        activity.runOnUiThread(updateGui)
    }

    // I decided to parse the JSON information here as well, since it's not like the raw JSON string
    // will be useful elsewhere - Leo.
    fun parseJson(json : String) : String {
        var res : String = ""
        var tempUnits : String = ""
        var windUnits : String = ""

        if (isMetric.equals("metric")) {
            tempUnits = "°C"
            windUnits = "km/h"
        } else {
            tempUnits = "°F"
            windUnits = "mph"
        }

        try {
            var jsonObject : JSONObject = JSONObject(json)
            var weatherData : JSONArray = jsonObject.getJSONArray("data")
            // They had an array with only one thing in it for some reason.
            var weatherData0 = weatherData.getJSONObject(0)

            // Retrieving the data that we need.
            var location = weatherData0.getString("city_name")
            var temperature = weatherData0.getString("temp")
            var windspeed = weatherData0.getString("wind_spd")

            // Combining the retrieved the data into something comprehensible.
            res = location + "\n" + temperature + tempUnits + "\n" + windspeed + windUnits

        } catch (e : JSONException) {
            Log.w("MainActivity", "Exception: " + e.message)
        }

        return res
    }

    inner class UpdateGui : Runnable {
        override fun run() {
            activity.updateView(parseJson(results))
        }
    }
}