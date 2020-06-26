package com.kok.roadtoyou.ui.addplan

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.kok.roadtoyou.ui.search.PlaceItem

data class AddPlaceItem(
    var date: Int?,     //0
    var count: Int?,    //1
    var title: String?, //3
    var id: Int?,       //2
    var type: Int?,     //4
    var latLng: String? //5
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    constructor(): this(null, null, null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(date)
        parcel.writeValue(count)
        parcel.writeString(title)
        parcel.writeValue(id)
        parcel.writeValue(type)
        parcel.writeString(latLng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddPlaceItem> {
        override fun createFromParcel(parcel: Parcel): AddPlaceItem {
            return AddPlaceItem(parcel)
        }

        override fun newArray(size: Int): Array<AddPlaceItem?> {
            return arrayOfNulls(size)
        }
    }
}