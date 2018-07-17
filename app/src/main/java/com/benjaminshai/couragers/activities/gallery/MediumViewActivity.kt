package com.benjaminshai.couragers.activities.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.activities.ActivityWithToolbar
import com.benjaminshai.couragers.beans.ImageInfo
import com.googlecode.flickrjandroid.Flickr
import com.googlecode.flickrjandroid.photos.Photo
import com.googlecode.flickrjandroid.photosets.Photoset

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList

class MediumViewActivity : ActivityWithToolbar() {

    private var viewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medium_view)
        attachToolbar()

        val intent = intent
        val position = intent.getIntExtra("position", 0)
        val imageUrls = intent.getStringArrayExtra("imageUrls")

        viewPager = findViewById<View>(R.id.pager) as ViewPager

        val mediumViewAdapter = MediumViewAdapter(this, imageUrls)
        viewPager!!.adapter = mediumViewAdapter
        viewPager!!.currentItem = position
    }

    inner class MediumViewAdapter(private val activity: Activity, private val list: Array<String>) : PagerAdapter() {
        private var imageView: ImageView? = null

        override fun getCount(): Int {
            return list.size
        }

        override fun isViewFromObject(view: View, o: Any): Boolean {
            return view === o as LinearLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewLayout = inflater.inflate(R.layout.layout_medium_view, container, false)

            imageView = viewLayout.findViewById<View>(R.id.imageView) as ImageView

            LoadImage(list[position], imageView!!).execute()

            (container as ViewPager).addView(viewLayout)
            return viewLayout
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as LinearLayout)
        }
    }

    private inner class LoadImage(private val url: String, private val imageView: ImageView) : AsyncTask<String, Int, Bitmap>() {

        override fun doInBackground(vararg params: String): Bitmap {
            var inputStream: InputStream? = null
            try {
                inputStream = URL(url).openStream()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return BitmapFactory.decodeStream(inputStream)
        }

        override fun onPostExecute(bitmap: Bitmap) {
            imageView.setImageBitmap(bitmap)
            super.onPostExecute(bitmap)
        }
    }
}
