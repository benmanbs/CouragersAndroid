package com.benjaminshai.couragers.activities

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Bundle

import com.benjaminshai.couragers.MyResultReceiver
import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.ResponseStatus
import com.benjaminshai.couragers.activities.schedule.DaysActivity
import com.benjaminshai.couragers.beans.Day
import com.benjaminshai.couragers.services.DayService

import java.util.ArrayList

class MainActivity : Activity(), MyResultReceiver.Receiver {

    lateinit var mReceiver: MyResultReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mReceiver = MyResultReceiver(Handler())
        mReceiver.setReceiver(this)
        val intent = Intent(Intent.ACTION_SYNC, null, this, DayService::class.java)
        intent.putExtra("receiver", mReceiver)
        intent.putExtra("command", "query")
        startService(intent)
    }

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        when (resultCode) {
            ResponseStatus.RUNNING -> {
            }
            ResponseStatus.FINISHED -> {
                val days = resultData.getParcelableArrayList<Day>("results")
                val myIntent = Intent(this, DaysActivity::class.java)
                myIntent.putParcelableArrayListExtra("days", days) //Optional parameters
                this.startActivity(myIntent)
            }
            ResponseStatus.ERROR -> {
                // if we error, make sure it goes to a page.
                val daysEmpty = ArrayList<Day>()
                val intent = Intent(this, DaysActivity::class.java)
                intent.putParcelableArrayListExtra("days", daysEmpty) //Optional parameters
                this.startActivity(intent)
            }
        }
    }
}
