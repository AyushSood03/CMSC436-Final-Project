package com.example.finalproject

// Depending on the data types of temperature and windspeed, these types may have to change.
class Weather {
    private var location : String = ""
    private var temperature : Float = 0.0f
    private var windspeed : Float = 0.0f

    // Which constructor ends up being used is up to whoever makes the search system to retrieve
    // the necessary data.
    constructor() {
        // This constructor will be empty.
    }

    constructor(loc : String, temp : Float, wind : Float) {
        // This constructor has the necessary components when declared.
        location = loc
        temperature = temp
        windspeed = wind
    }

    fun setLocation(loc : String) {
        location = loc
    }

    fun setTemperature(temp : Float) {
        temperature = temp
    }

    fun setWindspeed(wind : Float) {
        windspeed = wind
    }

    fun getLocation() : String {
        return location
    }

    fun getTemperature() : Float {
        return temperature
    }

    fun getWindspeed() : Float {
        return windspeed
    }
}