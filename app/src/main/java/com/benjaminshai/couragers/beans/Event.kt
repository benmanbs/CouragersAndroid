package com.benjaminshai.couragers.beans

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by bshai on 8/14/15.
 */
class Event(`in`: Parcel) : Parcelable {

    var title: String? = null
    var time: String? = null
    var hasMap: Int = 0
    var mapURL: String? = null
    var eventID: Int = 0
    var details: String? = null

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(time)
        dest.writeInt(hasMap)
        dest.writeString(mapURL)
        dest.writeInt(eventID)
        dest.writeString(details)
    }

    init {
        title = `in`.readString()
        time = `in`.readString()
        hasMap = `in`.readInt()
        mapURL = `in`.readString()
        eventID = `in`.readInt()
        details = `in`.readString()
    }

    companion object CREATOR: Parcelable.Creator<Event> {
        override fun createFromParcel(`in`: Parcel): Event {
            return Event(`in`)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}
