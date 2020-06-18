package com.kok.roadtoyou.ui.addplan

import android.os.Parcel
import android.os.Parcelable

data class PlanItem(
    var planID: String?,
    var planName: String?,
    var period: String?,
    var days: Int?,
    var userList: List<String>?,
    var placeList: List<AddPlaceItem>?
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createStringArrayList(),
        parcel.createTypedArrayList(AddPlaceItem)
    ) {
    }

    constructor(): this(null, null, null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(planID)
        parcel.writeString(planName)
        parcel.writeString(period)
        parcel.writeValue(days)
        parcel.writeStringList(userList)
        parcel.writeTypedList(placeList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlanItem> {
        override fun createFromParcel(parcel: Parcel): PlanItem {
            return PlanItem(parcel)
        }

        override fun newArray(size: Int): Array<PlanItem?> {
            return arrayOfNulls(size)
        }
    }


}