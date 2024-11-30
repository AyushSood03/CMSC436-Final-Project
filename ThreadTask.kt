package com.example.finalproject

class ThreadTask : Thread {

    private var results : String = "EMPTY STRING"
    private var locNum : Int = 0
    private lateinit var activity : SearchActivity

    constructor(activity : SearchActivity) {
        this.activity = activity
    }

    override fun run() {
        super.run()

        results = "CHANGED by the THREAD"

        var updateGui : UpdateGui = UpdateGui()
        activity.runOnUiThread(updateGui)
    }

    inner class UpdateGui : Runnable {
        override fun run() {
            activity.updateView(results, locNum)
        }
    }
}