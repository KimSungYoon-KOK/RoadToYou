package com.kok.roadtoyou.ui.search

import android.os.Parcel
import android.os.Parcelable

data class PlaceItem(
    val id: Int?,
    val title: String?,
    val type: Int?,
    val lng: Double?,
    val lat: Double?,
    val addr1: String?,
    val addr2: String?,
    val tel: String?,
    val url: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor():this(null, null, null, null, null, null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(title)
        parcel.writeValue(type)
        parcel.writeValue(lng)
        parcel.writeValue(lat)
        parcel.writeString(addr1)
        parcel.writeString(addr2)
        parcel.writeString(tel)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlaceItem> {
        override fun createFromParcel(parcel: Parcel): PlaceItem {
            return PlaceItem(parcel)
        }

        override fun newArray(size: Int): Array<PlaceItem?> {
            return arrayOfNulls(size)
        }
    }
}