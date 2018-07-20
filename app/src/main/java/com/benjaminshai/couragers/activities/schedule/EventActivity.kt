package com.benjaminshai.couragers.activities.schedule

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import com.benjaminshai.couragers.activities.ActivityWithToolbar
import com.benjaminshai.couragers.views.FontTextView
import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.beans.Day
import com.benjaminshai.couragers.beans.Event

import java.util.Arrays

class EventActivity : ActivityWithToolbar() {

    private var eventAdapter: EventAdapter? = null
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        attachToolbar()

        val intent = intent
        val day = intent.getParcelableExtra<Day>("day")

        listView = findViewById<View>(R.id.mainList) as ListView

        if (day.available == 0) {
            setBackgroundImage("event_bg_coming_soon_", day.dayID, R.id.backgroundView)
            setInvisible(R.id.headerView)
            setInvisible(R.id.footerView)
        } else {
            setBackgroundImage("event_bg_", day.dayID, R.id.backgroundView)
            setBackgroundImage("event_header_", day.dayID, R.id.headerView)
            setBackgroundImage("event_footer_", day.dayID, R.id.footerView)


            eventAdapter = EventAdapter(this, R.layout.event_row, Arrays.asList(*day.getSortedEvents()), Color.rgb(day.textRed, day.textGreen, day.textBlue))
            listView!!.adapter = eventAdapter
        }
        val empty = TextView(this)
        empty.height = 400
        listView!!.addFooterView(empty)
    }

    private fun setBackgroundImage(drawablePrefix: String, id: Int, viewId: Int) {
        val resourceId = resources.getIdentifier(drawablePrefix + id, "drawable",
                packageName)
        val image = resources.getDrawable(resourceId)
        val i = findViewById<View>(viewId) as ImageView
        i.visibility = View.VISIBLE
        i.setImageDrawable(image)
    }

    private fun setInvisible(viewId: Int) {
        val i = findViewById<View>(viewId) as ImageView
        i.visibility = View.INVISIBLE
    }

    private inner class EventAdapter(context: Context, resource: Int, private val events: List<Event>, private val color: Int) : ArrayAdapter<Event>(context, resource, events) {


        override fun getView(position: Int, view: View?, group: ViewGroup): View {
            var v = view

            val event = events[position]

            if (v == null) {

                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                v = inflater.inflate(R.layout.event_row, null)

            }

            val time = v!!.findViewById<View>(R.id.list_time_textview) as FontTextView

            if (time != null) {
                time.text = event.time
                time.setTextColor(color)
            }

            val title = v.findViewById<View>(R.id.list_title_textview) as FontTextView

            if (title != null) {
                title.text = event.title
                title.setTextColor(color)
            }

            val detail = v.findViewById<View>(R.id.list_details_textview) as FontTextView

            if (detail != null) {
                detail.text = event.details
                detail.setTextColor(color)
            }

            val mapButton = v.findViewById<View>(R.id.mapImage) as ImageView

            if (mapButton != null) {
                if (event.hasMap == 1) {
                    mapButton.setOnClickListener {
                        val i = Intent(this@EventActivity, MapActivity::class.java)
                        i.putExtra("mapImageUrl", event.mapURL)
                        startActivity(i)
                    }
                } else {
                    mapButton.visibility = View.GONE
                }
            }

            return v
        }
    }
}
