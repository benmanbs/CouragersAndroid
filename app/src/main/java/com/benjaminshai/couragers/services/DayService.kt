package com.benjaminshai.couragers.services

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver

import com.benjaminshai.couragers.BeanParser
import com.benjaminshai.couragers.ResponseStatus
import com.benjaminshai.couragers.beans.Day
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * Created by bshai on 8/13/15.
 */
class DayService : IntentService(TAG) {

    private val json: String
        @Throws(IOException::class)
        get() {

            val client = OkHttpClient()
            client.setConnectTimeout(10, TimeUnit.SECONDS)
            client.setWriteTimeout(10, TimeUnit.SECONDS)
            client.setReadTimeout(30, TimeUnit.SECONDS)

            val request = Request.Builder()
                    .url(DAYS_URL)
                    .build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body().string()

        }

    override fun onHandleIntent(intent: Intent?) {
        val receiver = intent!!.getParcelableExtra<ResultReceiver>("receiver")
        val command = intent.getStringExtra("command")
        val b = Bundle()
        if (command == "query") {
            receiver.send(ResponseStatus.RUNNING, Bundle.EMPTY)
            try {
                val JSON = json
                val days = BeanParser().parse(JSON)
                b.putParcelableArrayList("results", days)
                DaysSingleton.instance.days = days
                receiver.send(ResponseStatus.FINISHED, b)
            } catch (e: Exception) {
                b.putString(Intent.EXTRA_TEXT, e.toString())
                receiver.send(ResponseStatus.ERROR, b)
            }

        }

    }

    class DaysSingleton private constructor() {

        var days: List<Day>? = null

        init {
            days = ArrayList()
        }

        companion object {
            private var INSTANCE: DaysSingleton? = null

            val instance: DaysSingleton
                get() {
                    if (INSTANCE == null) {
                        INSTANCE = DaysSingleton()
                    }
                    return INSTANCE as DaysSingleton
                }
        }
    }

    companion object {

        private val TAG = "DayService"
        private val DAYS_URL = "http://teddysappserver.com:8080/api/days"
    }
}
