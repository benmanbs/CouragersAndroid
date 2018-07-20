package com.benjaminshai.couragers.beans

import android.os.Parcel
import android.os.Parcelable

import java.util.Arrays
import java.util.Comparator

/**
 * Created by bshai on 8/14/15.
 */
class Day(`in`: Parcel) : Parcelable {
    var dayID: Int = 0
    var displayName: String? = null
    var available: Int = 0
    var textRed: Int = 0
    var textGreen: Int = 0
    var textBlue: Int = 0
    var events: Array<Event>? = null

    fun getSortedEvents(): Array<out Event> {
        events ?: return arrayOf()
        return events!!.sortedArrayWith(compareBy(Event::eventID))
    }

    /* Parcelable Stuff */

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(dayID)
        dest.writeString(displayName)
        dest.writeInt(available)
        dest.writeInt(textRed)
        dest.writeInt(textGreen)
        dest.writeInt(textBlue)
        dest.writeTypedArray(events, 0)
    }

    init {
        dayID = `in`.readInt()
        displayName = `in`.readString()
        available = `in`.readInt()
        textRed = `in`.readInt()
        textGreen = `in`.readInt()
        textBlue = `in`.readInt()
        events = `in`.createTypedArray(Event.CREATOR)
    }

    companion object CREATOR: Parcelable.Creator<Day> {
        override fun createFromParcel(`in`: Parcel): Day {
            return Day(`in`)
        }

        override fun newArray(size: Int): Array<Day?> {
            return arrayOfNulls(size)
        }
    }
}
