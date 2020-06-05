package com.kok.roadtoyou.ui.search

data class Places(
    val id: Int?,
    val title: String?,
    val type: Int?,
    val posX: Double?,
    val posY: Double?,
    val addr1: String?,
    val addr2: String?,
    val tel: String?,
    val servertype: Int?,
    val url: String?
) {
    constructor():this(null, null, null, null, null, null, null, null, null, null)
}