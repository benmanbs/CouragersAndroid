package com.benjaminshai.couragers.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bshai on 8/14/15.
 */
public class Event implements Parcelable {

    private String title;
    private String time;
    private int hasMap;
    private String mapURL;
    private int eventID;
    private String details;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHasMap() {
        return hasMap;
    }

    public void setHasMap(int hasMap) {
        this.hasMap = hasMap;
    }

    public String getMapURL() {
        return mapURL;
    }

    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(time);
        dest.writeInt(hasMap);
        dest.writeString(mapURL);
        dest.writeInt(eventID);
        dest.writeString(details);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event(Parcel in) {
        title = in.readString();
        time = in.readString();
        hasMap = in.readInt();
        mapURL = in.readString();
        eventID = in.readInt();
        details = in.readString();
    }
}
