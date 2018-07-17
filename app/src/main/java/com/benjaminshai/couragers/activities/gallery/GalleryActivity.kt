package com.benjaminshai.couragers.activities.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView

import com.benjaminshai.couragers.Constants
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

class GalleryActivity : ActivityWithToolbar() {
    private var gridView: GridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        attachToolbar()

        val intent = intent
        val id = intent.getStringExtra("photosetId")

        initializeComponents()
        LoadImagesFromFlickrTask(id).execute()
    }

    private fun initializeComponents() {
        val spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2f, resources.displayMetrics)

        gridView = findViewById<View>(R.id.gridView) as GridView

        gridView!!.numColumns = 3
        gridView!!.setPadding(spacing.toInt(), spacing.toInt(), spacing.toInt(), spacing.toInt())
        gridView!!.verticalSpacing = spacing.toInt()
        gridView!!.horizontalSpacing = spacing.toInt()
    }

    private inner class LoadImagesFromFlickrTask(private val PHOTOSET_ID: String) : AsyncTask<String, Int, ArrayList<ImageInfo>>() {
        private val API_CODE = Constants.FLICKR_API_KEY
        private val SECRET_CODE = Constants.FLICKR_SECRET_KEY

        override fun doInBackground(vararg params: String): ArrayList<ImageInfo> {
            try {
                val flickr = Flickr(API_CODE, SECRET_CODE)
                val photos = flickr.photosetsInterface.getPhotos(PHOTOSET_ID, 0, 0)
                val result = ArrayList<ImageInfo>()
                for (photo in photos.photoList) {
                    val thumbnailUrl = photo.thumbnailUrl
                    val mediumUrl = photo.mediumUrl
                    var inputStreamThumbnail: InputStream? = null
                    try {
                        inputStreamThumbnail = URL(thumbnailUrl).openStream()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val bitmapThumbnail = BitmapFactory.decodeStream(inputStreamThumbnail)
                    result.add(ImageInfo(photo.title, bitmapThumbnail, mediumUrl))
                }
                return result
            } catch (e: Exception) {
                // do something, you fool!!
                return ArrayList()
            }

        }

        override fun onPostExecute(list: ArrayList<ImageInfo>) {
            // hide status bar
            this@GalleryActivity.findViewById<View>(R.id.progress_bar).visibility = View.GONE

            // if collection is empty, show error page.
            if (list.isEmpty()) {
                (this@GalleryActivity.findViewById<View>(R.id.galleries_error_view) as TextView).text = "Failed to load flickr album. Please try again."
                return
            }

            val imageGridViewAdapter = ImageGridViewAdapter(this@GalleryActivity, list)
            gridView!!.adapter = imageGridViewAdapter
            super.onPostExecute(list)
        }
    }

    inner class ImageGridViewAdapter(private val activity: Activity, private val list: List<ImageInfo>) : BaseAdapter() {

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val result: ImageView
            if (convertView == null)
                result = ImageView(activity)
            else
                result = convertView as ImageView

            result.scaleType = ImageView.ScaleType.CENTER_CROP
            result.minimumHeight = 300
            result.setImageBitmap(list[position].thumbnailBitmap)
            result.setOnClickListener(ImageGridViewCellOnClickListener(position))

            return result
        }

        internal inner class ImageGridViewCellOnClickListener(private val position: Int) : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(activity, MediumViewActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("imageUrls", getAllUrls(list))
                activity.startActivity(intent)
            }

            private fun getAllUrls(list: List<ImageInfo>): Array<String?> {
                val result = arrayOfNulls<String>(list.size)
                var counter = 0
                for (info in list) {
                    result[counter++] = info.mediumUrl
                }
                return result
            }
        }
    }
}
