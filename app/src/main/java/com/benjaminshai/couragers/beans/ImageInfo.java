package com.benjaminshai.couragers.beans;

import android.graphics.Bitmap;

/**
 * Created by bshai on 8/15/15.
 */
public class ImageInfo {
    private String name;
    private Bitmap thumbnailBitmap;
    private String mediumUrl;

    public ImageInfo(String name, Bitmap bitmap, String mediumUrl) {
        this.name = name;
        this.thumbnailBitmap = bitmap;
        this.mediumUrl = mediumUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getThumbnailBitmap() {
        return thumbnailBitmap;
    }

    public void setThumbnailBitmap(Bitmap thumbnailBitmap) {
        this.thumbnailBitmap = thumbnailBitmap;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }
}
