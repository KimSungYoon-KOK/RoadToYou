package com.kok.roadtoyou.ui.review

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class ReviewSummary(
    var list:ArrayList<LatLng>
)
data class ReviewInfo(
    val title:String,
    val date:String,
    val userId:String,
    val coverImg:String
)

data class ReviewItem(
    val placeName: String?,
    var review: String?,
    var hashTags: List<String>?,
    var imgList: ArrayList<String>?
){
    constructor(): this(null, null, null, null)
}