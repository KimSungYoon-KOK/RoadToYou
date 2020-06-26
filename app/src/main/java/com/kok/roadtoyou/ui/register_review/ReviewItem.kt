package com.kok.roadtoyou.ui.register_review

data class ReviewItem(
    val viewType: Int,
    val placeName: String?,
    val placeId: Int?,
    var review: String?,
    var hashTags: List<String>?,
    var imgList: MutableList<String>?
) {
    constructor(): this(1,null, null, null, null, null)
}