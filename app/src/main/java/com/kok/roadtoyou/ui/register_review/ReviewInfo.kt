package com.kok.roadtoyou.ui.register_review

data class ReviewInfo(
    val reviewName:String,
    val period:String,
    val reviewId:String,
    val planId: String,
    val coverImg:String,
    val userId: String,
    val favorites: Int,
    val reviewList: MutableList<ReviewItem>?
) {
    constructor(): this("title", "date", "reviewId", "planId","ImgURI", "userId", 0, null)
}