package com.benjaminshai.couragers.activities.image.upload

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout

import com.benjaminshai.couragers.R
import com.benjaminshai.couragers.activities.ActivityWithToolbar

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils

import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

class UploadActivity : ActivityWithToolbar() {

    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        attachToolbar()

        val upload = findViewById<View>(R.id.upload_image) as LinearLayout
        upload.setOnClickListener {
            val path = File(filesDir, "images/")
            if (!path.exists()) path.mkdirs()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            fileName = "JPEG_" + timeStamp + "_.jpg"
            val image = File(path, fileName!!)
            val imageUri = FileProvider.getUriForFile(this@UploadActivity, CAPTURE_IMAGE_FILE_PROVIDER, image)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        val gallery = findViewById<View>(R.id.gallery_image) as LinearLayout
        gallery.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, REQUEST_GALLERY)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val path = File(filesDir, "images/")
            if (!path.exists()) path.mkdirs()
            val imageFile = File(path, fileName!!)

            UploadToServer().execute(imageFile)
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                fileName = "JPEG_" + timeStamp + "_.jpg"

                val path = File(filesDir, "images/")
                if (!path.exists()) path.mkdirs()
                val file = File(path, fileName!!)
                val os = BufferedOutputStream(FileOutputStream(file))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.close()

                UploadToServer().execute(file)
            } catch (e: Exception) {
                // this sucks
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    inner class UploadToServer : AsyncTask<File, Int, Int>() {

        override fun doInBackground(vararg params: File): Int? {
            try {
                val client = DefaultHttpClient()
                val file = params[0]
                val post = HttpPost(IMAGE_UPLOAD_URL)

                val entityBuilder = MultipartEntityBuilder.create()
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                entityBuilder.addPart("file", FileBody(file))
                // add more key/value pairs here as needed

                val entity = entityBuilder.build()
                post.setEntity(entity)

                val response = client.execute(post)
                val httpEntity = response.getEntity()

                Log.v("result", EntityUtils.toString(httpEntity))
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }

            return 1
        }

        override fun onPostExecute(result: Int?) {
            // TODO flesh out some error handling in the UI.
            if (result == 1)
                println("Something worked!")
            else
                println("Didn't work")
            super.onPostExecute(result)
        }
    }

    companion object {

        private val REQUEST_IMAGE_CAPTURE = 1
        private val REQUEST_GALLERY = 2
        private val CAPTURE_IMAGE_FILE_PROVIDER = "com.benjaminshai.couragers.fileprovider"
        private val IMAGE_UPLOAD_URL = "http://teddysappserver.com:8080/api/images"
    }
}
