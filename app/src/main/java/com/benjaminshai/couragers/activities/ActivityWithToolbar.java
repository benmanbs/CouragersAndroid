package com.benjaminshai.couragers.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.benjaminshai.couragers.R;
import com.benjaminshai.couragers.activities.gallery.GallerySelector;
import com.benjaminshai.couragers.activities.image.upload.UploadActivity;
import com.benjaminshai.couragers.activities.schedule.DaysActivity;

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
                    Intent i = new Intent(ActivityWithToolbar.this, GallerySelector.class);
                    startActivity(i);
                }
            });
        }

        LinearLayout upload = (LinearLayout) findViewById(R.id.upload);

        if(upload != null) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ActivityWithToolbar.this, UploadActivity.class);
                        startActivity(i);
                    }
                });
            } else {
                upload.setVisibility(View.GONE);
            }
        }
    }
}
