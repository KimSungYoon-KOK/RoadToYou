package com.kok.roadtoyou.ui.member

import com.kok.roadtoyou.ui.addplan.PlanItem

data class User(
    val uid: String?,
    val name: String?,
    val planList: List<PlanItem>?,
    val reviewList: List<String>?
) {
    constructor(): this(null, null, null, null)
}