package com.benjaminshai.couragers.activities.schedule

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

import com.benjaminshai.couragers.MyResultReceiver
import com.benjaminshai.couragers.ResponseStatus
import com.benjaminshai.couragers.activities.ActivityWithToolbar
import com.benjaminshai.couragers.services.DayService
import com.benjaminshai.couragers.views.FontTextView
import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.beans.Day

import java.util.ArrayList

class DaysActivity : ActivityWithToolbar(), SwipeRefreshLayout.OnRefreshListener, MyResultReceiver.Receiver {

    private var listView: ListView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private val daySingleton = DayService.DaysSingleton.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days)
        attachToolbar()

        mSwipeRefreshLayout = findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener(this)

        listView = findViewById<View>(R.id.mainList) as ListView

        val intent = intent
        val days: List<Day>?
        if (daySingleton.days!!.isEmpty()) {
            days = intent.getParcelableArrayListExtra("days")
            daySingleton.days = days
        } else {
            days = daySingleton.days
        }


        val adapter = DayAdapter(this,
                R.layout.day_row, days)
        listView!!.adapter = adapter

        listView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            val i = Intent(this@DaysActivity, EventActivity::class.java)
            i.putExtra("day", days!![position])
            startActivity(i)
        }
    }

    override fun onRefresh() {
        val mReceiver = MyResultReceiver(Handler())
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
                this.daySingleton.days = days
                this.recreate()
                mSwipeRefreshLayout!!.isRefreshing = false
            }
            ResponseStatus.ERROR -> {
            }
        }
    }

    private inner class DayAdapter(context: Context, resource: Int, private val days: List<Day>?) : ArrayAdapter<Day>(context, resource, days) {


        override fun getView(position: Int, view: View?, group: ViewGroup): View {
            var v = view

            val day = days!![position]

            if (v == null) {

                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                v = inflater.inflate(R.layout.day_row, null)

            }

            val dayView = v!!.findViewById<View>(R.id.list_day_textview) as FontTextView

            dayView.text = day.displayName
            dayView.setTextColor(Color.rgb(day.textRed, day.textGreen, day.textBlue))

            return v
        }
    }
}
