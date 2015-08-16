package com.benjaminshai.couragers.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.benjaminshai.couragers.Constants;
import com.benjaminshai.couragers.R;

public class GallerySelector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_selector);

        TextView media = (TextView) findViewById(R.id.media_team);
        TextView couragers = (TextView) findViewById(R.id.couragers);

        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GallerySelector.this, GalleriesActivity.class);
                i.putExtra("collectionId", Constants.MEDIA_COLLECTION_ID);
                i.putExtra("collectionName", "Media Team");
                startActivity(i);
            }
        });

        couragers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GallerySelector.this, GalleriesActivity.class);
                i.putExtra("collectionId", Constants.KIDS_COLLECTION_ID);
                i.putExtra("collectionName", "Couragers");
                startActivity(i);
            }
        });
    }
}
