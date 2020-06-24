package com.kok.roadtoyou.ui.register_review

data class ReviewInfo(
    val reviewName:String,
    val period:String,
    val reviewId:String,
    val coverImg:String,
    val userId: String,
    val reviewList: MutableList<ReviewItem>?
) {
    constructor(): this("title", "date", "reviewId", "ImgURI", "userId", null)
}

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