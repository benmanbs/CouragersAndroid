package com.benjaminshai.couragers.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by bshai on 8/14/15.
 */
public class Day implements Parcelable{
    private int dayID;
    private String displayName;
    private int available;
    private int textRed;
    private int textGreen;
    private int textBlue;
    private Event[] events;

    public int getDayID() {
        return dayID;
    }

    public void setDayID(int dayID) {
        this.dayID = dayID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getTextRed() {
        return textRed;
    }

    public void setTextRed(int textRed) {
        this.textRed = textRed;
    }

    public int getTextGreen() {
        return textGreen;
    }

    public void setTextGreen(int textGreen) {
        this.textGreen = textGreen;
    }

    public int getTextBlue() {
        return textBlue;
    }

    public void setTextBlue(int textBlue) {
        this.textBlue = textBlue;
    }

    public Event[] getEvents() {
        Arrays.sort(this.events, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                return new Integer(lhs.getEventID()).compareTo(new Integer(rhs.getEventID()));
            }
        });
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }

    /* Parcelable Stuff */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dayID);
        dest.writeString(displayName);
        dest.writeInt(available);
        dest.writeInt(textRed);
        dest.writeInt(textGreen);
        dest.writeInt(textBlue);
        dest.writeTypedArray(events, 0);
    }

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    public Day(Parcel in) {
        dayID = in.readInt();
        displayName = in.readString();
        available = in.readInt();
        textRed = in.readInt();
        textGreen = in.readInt();
        textBlue = in.readInt();
        events = in.createTypedArray(Event.CREATOR);
    }
}
