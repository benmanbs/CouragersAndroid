package com.benjaminshai.couragers.activities.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.benjaminshai.couragers.Constants;
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
import java.util.List;

public class GalleryActivity extends ActivityWithToolbar {
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        attachToolbar();

        Intent intent = getIntent();
        String id = intent.getStringExtra("photosetId");

        initializeComponents();
        new LoadImagesFromFlickrTask(id).execute();
    }

    private void initializeComponents() {
        float spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2, getResources().getDisplayMetrics());

        gridView = (GridView)findViewById(R.id.gridView);

        gridView.setNumColumns(3);
        gridView.setPadding((int) spacing, (int) spacing, (int) spacing, (int) spacing);
        gridView.setVerticalSpacing((int) spacing);
        gridView.setHorizontalSpacing((int) spacing);
    }

    private class LoadImagesFromFlickrTask extends AsyncTask<String, Integer, ArrayList<ImageInfo>> {
        private final String API_CODE = Constants.FLICKR_API_KEY;
        private final String SECRET_CODE = Constants.FLICKR_SECRET_KEY;
        private final String PHOTOSET_ID;

        public LoadImagesFromFlickrTask(String id) {
            this.PHOTOSET_ID = id;
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
        protected ArrayList<ImageInfo> doInBackground(String... params) {
            try {
                Flickr flickr = new Flickr(API_CODE, SECRET_CODE);
                Photoset photos = flickr.getPhotosetsInterface().getPhotos(PHOTOSET_ID, 0, 0);
                ArrayList<ImageInfo> result = new ArrayList<ImageInfo>();
                for (Photo photo : photos.getPhotoList()) {
                    String thumbnailUrl = photo.getThumbnailUrl();
                    String mediumUrl = photo.getMediumUrl();
                    InputStream inputStreamThumbnail = null;
                    try {
                        inputStreamThumbnail = new URL(thumbnailUrl).openStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmapThumbnail = BitmapFactory.decodeStream(inputStreamThumbnail);
                    result.add(new ImageInfo(photo.getTitle(), bitmapThumbnail, mediumUrl));
                }
                return result;
            } catch (Exception e) {
                // do something, you fool!!
                return new ArrayList<ImageInfo>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ImageInfo> list) {
            // hide status bar
            GalleryActivity.this.findViewById(R.id.progress_bar).setVisibility(View.GONE);

            // if collection is empty, show error page.
            if (list.isEmpty()) {
                ((TextView)GalleryActivity.this.findViewById(R.id.galleries_error_view)).setText("Failed to load flickr album. Please try again.");
                return;
            }

            ImageGridViewAdapter imageGridViewAdapter = new ImageGridViewAdapter(GalleryActivity.this, list);
            gridView.setAdapter(imageGridViewAdapter);
            super.onPostExecute(list);
        }
    }

    public class ImageGridViewAdapter extends BaseAdapter {

        private Activity activity;
        private List<ImageInfo> list;

        public ImageGridViewAdapter(Activity activity, List<ImageInfo> list) {
            this.activity = activity;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView result ;
            if (convertView == null)
                result = new ImageView(activity);
            else
                result = (ImageView) convertView;

            result.setScaleType(ImageView.ScaleType.CENTER_CROP);
            result.setMinimumHeight(300);
            result.setImageBitmap(list.get(position).getThumbnailBitmap());
            result.setOnClickListener(new ImageGridViewCellOnClickListener(position));

            return  result;
        }

        class ImageGridViewCellOnClickListener implements View.OnClickListener {
            private int position;

            public ImageGridViewCellOnClickListener(int position) {
                this.position = position;
            }
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MediumViewActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("imageUrls", getAllUrls(list));
                activity.startActivity(intent);
            }

            private String[] getAllUrls(List<ImageInfo> list) {
                String[] result = new String[list.size()];
                int counter = 0;
                for (ImageInfo info : list) {
                    result[counter++] = info.getMediumUrl();
                }
                return result;
            }
        }
    }
}
