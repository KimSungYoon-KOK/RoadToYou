package com.kok.roadtoyou.ui.addplan

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

data class PlanItem(
    var planId: String?,
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

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "planId" to planId,
            "planName" to planName,
            "period" to period,
            "days" to days,
            "userList" to userList,
            "placeList" to placeList
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(planId)
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