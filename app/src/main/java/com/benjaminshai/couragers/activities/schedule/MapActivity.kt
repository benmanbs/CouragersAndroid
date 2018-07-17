package com.benjaminshai.couragers.activities.schedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast

import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.activities.ActivityWithToolbar

import java.io.IOException
import java.io.InputStream

import it.sephiroth.android.library.imagezoom.ImageViewTouch
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase

class MapActivity : ActivityWithToolbar() {

    internal lateinit var mImage: ImageViewTouch
    private var mapURL: String? = null


    internal var imageMatrix: Matrix? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapURL = intent.getStringExtra("mapImageUrl")

        setContentView(R.layout.activity_map)

        attachToolbar()

    }

    override fun onContentChanged() {
        super.onContentChanged()
        mImage = findViewById<View>(R.id.image) as ImageViewTouch
        // set the default image display type
        mImage.displayType = ImageViewTouchBase.DisplayType.FIT_IF_BIGGER


        selectImage(mapURL)
    }

    private fun setBackgroundColor(color: Int) {
        mImage.setBackgroundColor(color)
    }

    fun selectImage(nameURL: String?) {

        val name = nameURL!! + ".png"

        var bitmap: Bitmap? = null // = DecodeUtils.decode(this, imageUri, size, size);

        var bitmapStream: InputStream? = null

        try {
            bitmapStream = assets.open("maps/$name")
            bitmap = BitmapFactory.decodeStream(bitmapStream)
            bitmapStream!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        if (null != bitmap) {
            // calling this will force the image to fit the ImageView container width/height

            if (null == imageMatrix) {
                imageMatrix = Matrix()
            } else {
                // get the current image matrix, if we want restore the
                // previous matrix once the bitmap is changed
                // imageMatrix = mImage.getDisplayMatrix();
            }

            mImage.setImageBitmap(bitmap, if (imageMatrix!!.isIdentity) null else imageMatrix, ImageViewTouchBase.ZOOM_INVALID, ImageViewTouchBase.ZOOM_INVALID)

        } else {
            Toast.makeText(this, "Failed to load the image", Toast.LENGTH_LONG).show()
        }
    }
}
