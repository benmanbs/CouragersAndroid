package com.benjaminshai.couragers.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.benjaminshai.couragers.Constants;
import com.benjaminshai.couragers.R;

/**
 * Created by bshai on 8/15/15.
 */
public abstract class ActivityWithToolbar extends AppCompatActivity {


    protected void attachToolbar() {
        LinearLayout schedule = (LinearLayout) findViewById(R.id.schedule);

        if (schedule != null) {
            schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ActivityWithToolbar.this, DaysActivity.class);
                    startActivity(i);
                }
            });
        }

        LinearLayout gallery = (LinearLayout) findViewById(R.id.gallery);

        if(gallery != null) {
            gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ActivityWithToolbar.this, GalleriesActivity.class);
                    i.putExtra("collectionId", Constants.COLLECTION_ID);
                    startActivity(i);
                }
            });
        }
    }
}
