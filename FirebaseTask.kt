package com.example.finalproject

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseTask: Thread {
    private var activity : SearchActivity
    private var firebase: FirebaseDatabase
    private var reference: DatabaseReference
    private var key: String

    constructor(activity: SearchActivity, lon: String, lat: String) {
        this.activity = activity
        this.firebase = FirebaseDatabase.getInstance()
        this.reference = firebase.getReference()
        this.key = "$lon,$lat"
    }

    override fun run() {
        super.run()
        Log.w("MainActivity", "Start of FirebaseTask.run()")
        this.reference.child(this.key).get().addOnSuccessListener {
            Log.w("MainActivity", "Before Updating Child")
            this.reference.child(this.key).setValue((it.value.toString().toInt() + 1).toString())
            Log.w("MainActivity", "After Updating Child")
        }.addOnFailureListener{
            Log.w("MainActivity", "Before Adding New Child")
            this.reference.child(this.key).setValue(1)
            Log.w("MainActivity", "After Adding New Child")
        }
        Log.w("MainActivity", "End of FirebaseTask.run()")
    }
}