package com.benjaminshai.couragers.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout

import com.benjaminshai.couragers.Constants
import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.activities.gallery.GalleriesActivity
import com.benjaminshai.couragers.activities.image.upload.UploadActivity
import com.benjaminshai.couragers.activities.schedule.DaysActivity

/**
 * Created by bshai on 8/15/15.
 */
abstract class ActivityWithToolbar : AppCompatActivity() {


    protected fun attachToolbar() {
        val schedule = findViewById<View>(R.id.schedule) as LinearLayout

        schedule?.setOnClickListener {
            val i = Intent(this@ActivityWithToolbar, DaysActivity::class.java)
            startActivity(i)
        }

        val gallery = findViewById<View>(R.id.gallery) as LinearLayout

        gallery?.setOnClickListener {
            val i = Intent(this@ActivityWithToolbar, GalleriesActivity::class.java)
            i.putExtra("collectionId", Constants.KIDS_COLLECTION_ID)
            startActivity(i)
        }

        val upload = findViewById<View>(R.id.upload) as LinearLayout

        if (upload != null) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                upload.setOnClickListener {
                    val i = Intent(this@ActivityWithToolbar, UploadActivity::class.java)
                    startActivity(i)
                }
            } else {
                upload.visibility = View.GONE
            }
        }
    }
}
