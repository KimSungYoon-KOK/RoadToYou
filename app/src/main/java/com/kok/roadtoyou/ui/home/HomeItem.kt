package com.kok.roadtoyou.ui.home

data class HomeItem(
    val thumbnail:String,
    val title:String,
    val content:String,
    val mem:String,
    val likes:String,
    val reviewID:String ) {
    constructor():this("","","","","","")
}