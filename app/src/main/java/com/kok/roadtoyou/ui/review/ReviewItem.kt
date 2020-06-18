package com.kok.roadtoyou.ui.review

import com.google.android.gms.maps.model.LatLng
import com.kok.roadtoyou.ui.addplan.AddPlaceItem

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
    val review: List<String>?,
    val hashTags: List<String>?,
    var imgList: MutableList<String>?
){
    constructor(): this(null, null, null, null)
}