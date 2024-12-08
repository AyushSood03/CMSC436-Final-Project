package com.example.finalproject

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// "Leo, did you really just make a Kotlin file that is basically the same as ApiTask for a very
// slight convenience?" Yes, I did.
class HomeApiTask : Thread {
    private var results : String = "EMPTY STRING"
    private lateinit var activity : MainActivity

    private var lon : String = "0.0"
    private var lat : String = "0.0"
    private var isMetric : String = "imperial"
    private var locNum : Int = 0

    private var firebase: FirebaseDatabase
    private var reference: DatabaseReference

    private var topKey: String = ""
    private var secondTopKey: String = ""

    private var shittyDelayThing2 = true // Following in the footsteps of Leo, our fearless leader. -Ayush
    private var dummyVal: Int = 0
    private var forTrending : Boolean = false

    constructor(activity : MainActivity, lon : String, lat : String, isMetric : Boolean, locNum : Int, forTrending : Boolean) {
        this.activity = activity

        this.lon = lon
        this.lat = lat
        this.locNum = locNum
        this.forTrending = forTrending

        // This is used to change the output between metric and imperial.
        if (isMetric) this.isMetric = "metric"
        else this.isMetric = "imperial"

        this.firebase = FirebaseDatabase.getInstance()
        this.reference = firebase.getReference()
    }

    override fun run() {
        super.run()

        // API key is: ea7c59639bmshfe35825cbd1a3f4p13245djsn536e148d52f6
        try {
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

        Log.w("MainActivity", "HomeApiTask Before reading firebase")
        Log.w("MainActivity", "shittyDelayThing2: $shittyDelayThing2")

        val listener: TrendListener = TrendListener()
        reference.addValueEventListener(listener)
        reference.child("dummy").setValue(dummyVal.toString())

        Thread.sleep(1000L)

        while (shittyDelayThing2) {
            //dilly dally until variables get updated
        }

        reference.child("dummy").removeValue()
        reference.removeEventListener(listener)
        shittyDelayThing2 = true

        Log.w("MainActivity", "Top Key: $topKey")
        Log.w("MainActivity", "Second Top Key: $secondTopKey")

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

    inner class TrendListener: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var topValue: Int = 0
            var secondTopValue: Int = 0
            for (childShapshot in snapshot.children) {
                val childKey = childShapshot.key.toString()
                val childValue = childShapshot.value.toString().toInt()
                //Log.w("MainActivity", "--------------------------------------------")
                //Log.w("MainActivity", "childKey: $childKey, childValue: $childValue")
                //Log.w("MainActivity", "topKey: $topKey, topValue: $topValue")
                if (childValue > topValue) {
                    topValue = childValue
                    this@HomeApiTask.secondTopKey = topKey
                    this@HomeApiTask.topKey = childKey
                }
                if ((childValue > secondTopValue) && (childKey != topKey)) {
                    secondTopValue = childValue
                    this@HomeApiTask.secondTopKey = childKey
                }
                //.w("MainActivity", "childKey: $childKey, childValue: $childValue")
                //Log.w("MainActivity", "topKey: $topKey, topValue: $topValue")
            }
            shittyDelayThing2 = false
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("MainActivity", "Error reading data: $error")
        }

    }

    inner class UpdateGui : Runnable {
        override fun run() {
            if (!forTrending)
                activity.updateView(parseJson(results), locNum, topKey, secondTopKey)
            else
                activity.updateTrending(parseJson(results), locNum)
        }
    }
}