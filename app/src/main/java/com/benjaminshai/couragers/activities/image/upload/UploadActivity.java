package com.benjaminshai.couragers.activities.image.upload;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.benjaminshai.couragers.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.benjaminshai.couragers.fileprovider";
    private static final String IMAGE_UPLOAD_URL = "http://teddysappserver.com:8080/api/images";

    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        LinearLayout upload = (LinearLayout) findViewById(R.id.upload_image);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(getFilesDir(), "images/");
                if (!path.exists()) path.mkdirs();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                fileName = "JPEG_" + timeStamp + "_.jpg";
                File image = new File(path, fileName);
                Uri imageUri = FileProvider.getUriForFile(UploadActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, image);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        LinearLayout gallery = (LinearLayout) findViewById(R.id.gallery_image);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            File path = new File(getFilesDir(), "images/");
            if (!path.exists()) path.mkdirs();
            File imageFile = new File(path, fileName);

            new UploadToServer().execute(imageFile);
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                fileName = "JPEG_" + timeStamp + "_.jpg";

                File path = new File(getFilesDir(), "images/");
                if (!path.exists()) path.mkdirs();
                File file = new File(path, fileName);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();

                new UploadToServer().execute(file);
            } catch (Exception e) {
                // this sucks
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class UploadToServer extends AsyncTask<File, Integer, Integer> {

        @Override
        protected Integer doInBackground(File... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                File file = params[0];
                HttpPost post = new HttpPost(IMAGE_UPLOAD_URL);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addPart("file", new FileBody(file));
                // add more key/value pairs here as needed

                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);

                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();

                Log.v("result", EntityUtils.toString(httpEntity));
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO flesh out some error handling in the UI.
            if (result == 1)
                System.out.println("Something worked!");
            else
                System.out.println("Didn't work");
            super.onPostExecute(result);
        }
    }
}
