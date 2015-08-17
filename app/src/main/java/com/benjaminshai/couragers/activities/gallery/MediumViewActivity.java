package com.benjaminshai.couragers.activities.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benjaminshai.couragers.R;
import com.benjaminshai.couragers.activities.ActivityWithToolbar;
import com.benjaminshai.couragers.beans.ImageInfo;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photosets.Photoset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MediumViewActivity extends ActivityWithToolbar {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medium_view);
        attachToolbar();

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        String[] imageUrls = intent.getStringArrayExtra("imageUrls");

        viewPager = (ViewPager) findViewById(R.id.pager);

        MediumViewAdapter mediumViewAdapter = new MediumViewAdapter(this, imageUrls);
        viewPager.setAdapter(mediumViewAdapter);
        viewPager.setCurrentItem(position);
    }

    public class MediumViewAdapter extends PagerAdapter {

        private Activity activity;
        private ImageView imageView;
        private String[] list;

        public MediumViewAdapter(Activity activity, String[] list) {
            this.activity = activity;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == (LinearLayout)o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.layout_medium_view, container, false);

            imageView = (ImageView) viewLayout.findViewById(R.id.imageView);

            new LoadImage(list[position], imageView).execute();

            ((ViewPager) container).addView(viewLayout);
            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((LinearLayout)object);
        }
    }

    private class LoadImage extends AsyncTask<String, Integer, Bitmap> {

        private String url;
        private ImageView imageView;

        public LoadImage(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                inputStream = new URL(url).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmapMedium = BitmapFactory.decodeStream(inputStream);
            return bitmapMedium;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }
}
