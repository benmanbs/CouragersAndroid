package com.benjaminshai.couragers.activities.schedule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.benjaminshai.couragers.R;
import com.benjaminshai.couragers.activities.ActivityWithToolbar;

import java.io.IOException;
import java.io.InputStream;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class MapActivity extends ActivityWithToolbar {

    ImageViewTouch mImage;
    private String mapURL;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        mapURL = getIntent().getStringExtra("mapImageUrl");

        setContentView(R.layout.activity_map);

        attachToolbar();

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mImage = (ImageViewTouch) findViewById(R.id.image);
        // set the default image display type
        mImage.setDisplayType( ImageViewTouchBase.DisplayType.FIT_IF_BIGGER );


        selectImage(mapURL);
    }

    private void setBackgroundColor(int color) {
        mImage.setBackgroundColor(color);
    }


    Matrix imageMatrix;

    public void selectImage(String nameURL) {

        String name = nameURL + ".png";

        Bitmap bitmap = null; // = DecodeUtils.decode(this, imageUri, size, size);

        InputStream bitmapStream = null;

        try {
            bitmapStream=getAssets().open("maps/"+name);
            bitmap = BitmapFactory.decodeStream(bitmapStream);
            bitmapStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if( null != bitmap )
        {
            // calling this will force the image to fit the ImageView container width/height

            if( null == imageMatrix ) {
                imageMatrix = new Matrix();
            } else {
                // get the current image matrix, if we want restore the
                // previous matrix once the bitmap is changed
                // imageMatrix = mImage.getDisplayMatrix();
            }

            mImage.setImageBitmap( bitmap, imageMatrix.isIdentity() ? null : imageMatrix, ImageViewTouchBase.ZOOM_INVALID, ImageViewTouchBase.ZOOM_INVALID );

        } else {
            Toast.makeText(this, "Failed to load the image", Toast.LENGTH_LONG).show();
        }
    }
}
