package com.benjaminshai.couragers.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.benjaminshai.couragers.Constants;
import com.benjaminshai.couragers.R;
import com.benjaminshai.couragers.views.FontTextView;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GalleriesActivity extends ActivityWithToolbar {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleries);
        attachToolbar();

        Intent intent = getIntent();
        String collectionId = intent.getStringExtra("collectionId");
        String collectionName = intent.getStringExtra("collectionName");

        listView = (ListView) findViewById(R.id.mainList);
        FontTextView albumName = (FontTextView) findViewById(R.id.album_name);
        albumName.setText(collectionName);

        new LoadAlbumsFromFlickr(collectionId).execute();
    }

    private class LoadAlbumsFromFlickr extends AsyncTask<String, Integer, ArrayList<PhotosetData>> {
        private final String API_CODE = Constants.FLICKR_API_KEY;
        private final String SECRET_CODE = Constants.FLICKR_SECRET_KEY;
        private final String FLICKR_URL;

        public LoadAlbumsFromFlickr(String collectionID) {
            this.FLICKR_URL = "https://api.flickr.com/services/rest/?method=" +
                    "flickr.collections.getTree&api_key=" + API_CODE + "&user_id=" + Constants.USER_ID +
                    "&collection_id=" + collectionID+ "&format=json&nojsoncallback=1";
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
        protected ArrayList<PhotosetData> doInBackground(String... params) {
            try {
                Flickr flickr = new Flickr(API_CODE, SECRET_CODE);

                //Flickr lib is being a bitch about getting collections. Do it manually.
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                client.setWriteTimeout(10, TimeUnit.SECONDS);
                client.setReadTimeout(30, TimeUnit.SECONDS);

                Request request = new Request.Builder()
                        .url(FLICKR_URL)
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);


                JSONObject jsonObject = new JSONObject(response.body().string());

                List<PhotoSetInfo> photoSets = parseJSON(jsonObject);

                ArrayList<PhotosetData> result = new ArrayList<PhotosetData>();

                for (PhotoSetInfo set : photoSets) {
                    String description = set.description;
                    String id = set.id;

                    Photoset innerSet = flickr.getPhotosetsInterface().getPhotos(id, 0, 0);
                    // get the first three pictures, and store them
                    PhotoInfo[] info = new PhotoInfo[3];
                    String[] urls = new String[innerSet.getPhotoList().size()];
                    for (int i = 0; i < innerSet.getPhotoList().size(); i++ ) {
                        if (i < 3) {
                            Photo photo = innerSet.getPhotoList().get(i);

                            String thumbnailUrl = photo.getThumbnailUrl();
                            String mediumUrl = photo.getMediumUrl();
                            InputStream inputStreamThumbnail = null;
                            try {
                                inputStreamThumbnail = new URL(thumbnailUrl).openStream();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bitmap bitmapThumbnail = BitmapFactory.decodeStream(inputStreamThumbnail);

                            info[i] = new PhotoInfo(bitmapThumbnail, mediumUrl);
                        }
                        urls[i] = innerSet.getPhotoList().get(i).getMediumUrl();
                    }

                    result.add(new PhotosetData(description, id, info, urls));
                }
                return result;
            } catch (Exception e) {
                // do something, you fool!!
                return null;
            }
        }

        private List<PhotoSetInfo> parseJSON(JSONObject json) throws Exception {
            JSONArray array = json.getJSONObject("collections")
                    .getJSONArray("collection")
                    .getJSONObject(0)
                    .getJSONArray("set");

            List<PhotoSetInfo> list = new ArrayList<PhotoSetInfo>();
            for (int i = 0; i < array.length(); i++ ) {
                JSONObject item = array.getJSONObject(i);
                list.add(new PhotoSetInfo(item.getString("id"), item.getString("description")));
            }

            return list;
        }

        public class PhotoSetInfo {
            private String id;
            private String description;

            public PhotoSetInfo(String id, String description) {
                this.id = id;
                this.description = description;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<PhotosetData> list) {
            GalleriesAdapter galleriesAdapter = new GalleriesAdapter(GalleriesActivity.this, R.layout.gallery_row, list);
            listView.setAdapter(galleriesAdapter);
            super.onPostExecute(list);
        }
    }

    public class PhotosetData {
        private String description;
        private String id;
        private PhotoInfo[] photoInfo;
        private String[] urls;

        public PhotosetData(String description, String id, PhotoInfo[] photoInfo, String[] urls) {
            this.description = description;
            this.id = id;
            this.photoInfo = photoInfo;
            this.urls = urls;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public PhotoInfo[] getPhotoInfo() {
            return photoInfo;
        }

        public void setPhotoInfo(PhotoInfo[] photoInfo) {
            this.photoInfo = photoInfo;
        }

        public String[] getUrls() {
            return urls;
        }

        public void setUrls(String[] urls) {
            this.urls = urls;
        }
    }

    public class PhotoInfo {
        private Bitmap photo;
        private String mediumUrl;

        public PhotoInfo(Bitmap photo, String mediumUrl) {
            this.photo = photo;
            this.mediumUrl = mediumUrl;
        }

        public Bitmap getPhoto() {
            return photo;
        }

        public void setPhoto(Bitmap photo) {
            this.photo = photo;
        }

        public String getMediumUrl() {
            return mediumUrl;
        }

        public void setMediumUrl(String mediumUrl) {
            this.mediumUrl = mediumUrl;
        }
    }

    public class GalleriesAdapter extends ArrayAdapter<PhotosetData> {

        private List<PhotosetData> list;
        private Context context;

        public GalleriesAdapter(Context context, int resource, List<PhotosetData> list) {
            super(context, resource, list);
            this.list = list;
            this.context = context;
        }

        public class MyHolder {
            FontTextView day;
            RelativeLayout topBar;
            ImageView imageView1;
            ImageView imageView2;
            ImageView imageView3;


            public MyHolder(View v) {
                day = (FontTextView) v.findViewById(R.id.dayTitle);
                topBar = (RelativeLayout) v.findViewById(R.id.topBar);
                imageView1 = (ImageView) v.findViewById(R.id.image1);
                imageView2 = (ImageView) v.findViewById(R.id.image2);
                imageView3 = (ImageView) v.findViewById(R.id.image3);
            }
        }

        @Override
        public View getView(int position, View view, ViewGroup group) {
            View v = view;

            final PhotosetData photos = list.get(position);

            MyHolder holder;

            if(v == null){

                LayoutInflater inflater = LayoutInflater.from(context);

                v = inflater.inflate(R.layout.gallery_row, group, false);

                holder = new MyHolder(v);
                v.setTag(holder);
            } else {
                holder = (MyHolder) v.getTag();
            }

            FontTextView day = holder.day;

            if (day != null) {
                day.setText(photos.getDescription());
            }

            RelativeLayout topBar = holder.topBar;

            if (topBar != null) {
                topBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GalleriesActivity.this, GalleryActivity.class);
                        intent.putExtra("photosetId", photos.getId());
                        GalleriesActivity.this.startActivity(intent);
                    }
                });
            }

            int counter = 1;
            for (final PhotoInfo info : photos.getPhotoInfo()) {

                final ImageView imageView;
                switch(counter++) {
                    case 1:
                        imageView = holder.imageView1;
                        break;
                    case 2:
                        imageView = holder.imageView2;
                        break;
                    case 3:
                        imageView = holder.imageView3;
                        break;
                    default:
                        imageView = null;
                }

                if (imageView != null) {
                    if (info == null) {
                        imageView.setVisibility(View.INVISIBLE);
                        imageView.setOnClickListener(null);
                        continue;
                    }

                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(info.getPhoto());
                    final int imagePosition = counter - 1;
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GalleriesActivity.this, MediumViewActivity.class);
                            intent.putExtra("position", imagePosition);
                            intent.putExtra("imageUrls", photos.getUrls());
                            GalleriesActivity.this.startActivity(intent);
                        }
                    });
                }
            }

            return v;
        }
    }
}
