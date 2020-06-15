package com.kok.roadtoyou.ui.member

data class User(
    val uid: String?,
    val name: String?,
    val planList: List<String>?,
    val reviewList: List<String>?
) {
    constructor(): this(null, null, null, null)
}