package com.benjaminshai.couragers.activities.gallery

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView

import com.benjaminshai.couragers.Constants
import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.activities.ActivityWithToolbar
import com.benjaminshai.couragers.views.FontTextView
import com.googlecode.flickrjandroid.Flickr
import com.googlecode.flickrjandroid.photos.Photo
import com.googlecode.flickrjandroid.photosets.Photoset
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

import org.json.JSONArray
import org.json.JSONObject

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class GalleriesActivity : ActivityWithToolbar() {

    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galleries)
        attachToolbar()

        val intent = intent
        val collectionId = intent.getStringExtra("collectionId")

        listView = findViewById<View>(R.id.mainList) as ListView

        LoadAlbumsFromFlickr(collectionId).execute()
    }

    private inner class LoadAlbumsFromFlickr(collectionID: String) : AsyncTask<String, Int, ArrayList<PhotosetData>>() {
        private val API_CODE = Constants.FLICKR_API_KEY
        private val SECRET_CODE = Constants.FLICKR_SECRET_KEY
        private val FLICKR_URL: String

        init {
            this.FLICKR_URL = "https://api.flickr.com/services/rest/?method=" +
                    "flickr.collections.getTree&api_key=" + API_CODE + "&user_id=" + Constants.USER_ID +
                    "&collection_id=" + collectionID + "&format=json&nojsoncallback=1"
        }

        override fun doInBackground(vararg params: String): ArrayList<PhotosetData> {
            try {
                val flickr = Flickr(API_CODE, SECRET_CODE)

                //Flickr lib is being a bitch about getting collections. Do it manually.
                val client = OkHttpClient()
                client.setConnectTimeout(10, TimeUnit.SECONDS)
                client.setWriteTimeout(10, TimeUnit.SECONDS)
                client.setReadTimeout(30, TimeUnit.SECONDS)

                val request = Request.Builder()
                        .url(FLICKR_URL)
                        .build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")


                val jsonObject = JSONObject(response.body().string())

                val photoSets = parseJSON(jsonObject)

                val result = ArrayList<PhotosetData>()

                for (set in photoSets) {
                    val description = set.description
                    val id = set.id

                    val innerSet = flickr.photosetsInterface.getPhotos(id, 0, 0)
                    // get the first three pictures, and store them
                    val info = arrayOfNulls<PhotoInfo>(3)
                    val urls = arrayOfNulls<String>(innerSet.photoList.size)
                    for (i in 0 until innerSet.photoList.size) {
                        if (i < 3) {
                            val photo = innerSet.photoList[i]

                            val thumbnailUrl = photo.thumbnailUrl
                            val mediumUrl = photo.mediumUrl
                            var inputStreamThumbnail: InputStream? = null
                            try {
                                inputStreamThumbnail = URL(thumbnailUrl).openStream()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            val bitmapThumbnail = BitmapFactory.decodeStream(inputStreamThumbnail)

                            info[i] = PhotoInfo(bitmapThumbnail, mediumUrl)
                        }
                        urls[i] = innerSet.photoList[i].mediumUrl
                    }

                    result.add(PhotosetData(description, id, info, urls))
                }
                return result
            } catch (e: Exception) {
                // return an empty list so we don't NPE
                return ArrayList()
            }

        }

        @Throws(Exception::class)
        private fun parseJSON(json: JSONObject): List<PhotoSetInfo> {
            val array = json.getJSONObject("collections")
                    .getJSONArray("collection")
                    .getJSONObject(0)
                    .getJSONArray("set")

            val list = ArrayList<PhotoSetInfo>()
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                list.add(PhotoSetInfo(item.getString("id"), item.getString("description")))
            }

            return list
        }

        inner class PhotoSetInfo(val id: String, val description: String)

        override fun onPostExecute(list: ArrayList<PhotosetData>) {
            // hide status bar
            this@GalleriesActivity.findViewById<View>(R.id.progress_bar).visibility = View.GONE

            // if collection is empty, show error page.
            if (list.isEmpty()) {
                (this@GalleriesActivity.findViewById<View>(R.id.galleries_error_view) as TextView).text = "Failed to load flickr albums. Please try again."
                return
            }

            val galleriesAdapter = GalleriesAdapter(this@GalleriesActivity, R.layout.gallery_row, list)
            listView!!.adapter = galleriesAdapter
            super.onPostExecute(list)
        }
    }

    inner class PhotosetData (
        val description: String?,
        val id: String?,
        val photoInfo: Array<PhotoInfo?>,
        val urls: Array<String?>)

    inner class PhotoInfo(var photo: Bitmap?, var mediumUrl: String?)

    inner class GalleriesAdapter(context: Context, resource: Int, private val list: List<PhotosetData>) : ArrayAdapter<PhotosetData>(context, resource, list) {

        inner class MyHolder(v: View) {
            internal var day: FontTextView? = null
            internal var topBar: RelativeLayout? = null
            internal var imageView1: ImageView
            internal var imageView2: ImageView
            internal var imageView3: ImageView


            init {
                day = v.findViewById<View>(R.id.dayTitle) as FontTextView
                topBar = v.findViewById<View>(R.id.topBar) as RelativeLayout
                imageView1 = v.findViewById<View>(R.id.image1) as ImageView
                imageView2 = v.findViewById<View>(R.id.image2) as ImageView
                imageView3 = v.findViewById<View>(R.id.image3) as ImageView
            }
        }

        override fun getView(position: Int, view: View?, group: ViewGroup): View {
            var v = view

            val photos = list[position]

            val holder: MyHolder

            if (v == null) {

                val inflater = LayoutInflater.from(context)

                v = inflater.inflate(R.layout.gallery_row, group, false)

                holder = MyHolder(v!!)
                v.tag = holder
            } else {
                holder = v.tag as MyHolder
            }

            val day = holder.day

            if (day != null) {
                day.text = photos.description
            }

            val topBar = holder.topBar

            topBar?.setOnClickListener {
                val intent = Intent(this@GalleriesActivity, GalleryActivity::class.java)
                intent.putExtra("photosetId", photos.id)
                this@GalleriesActivity.startActivity(intent)
            }

            var counter = 1
            for (info in photos.photoInfo!!) {

                val imageView: ImageView?
                when (counter++) {
                    1 -> imageView = holder.imageView1
                    2 -> imageView = holder.imageView2
                    3 -> imageView = holder.imageView3
                    else -> imageView = null
                }

                if (imageView != null) {
                    if (info == null) {
                        imageView.visibility = View.INVISIBLE
                        imageView.setOnClickListener(null)
                        continue
                    }

                    imageView.visibility = View.VISIBLE
                    imageView.setImageBitmap(info.photo)
                    val imagePosition = counter - 1
                    imageView.setOnClickListener {
                        val intent = Intent(this@GalleriesActivity, MediumViewActivity::class.java)
                        intent.putExtra("position", imagePosition)
                        intent.putExtra("imageUrls", photos.urls)
                        this@GalleriesActivity.startActivity(intent)
                    }
                }
            }

            return v
        }
    }
}
