package com.kok.roadtoyou.ui.addplan

import com.kok.roadtoyou.ui.search.PlaceItem

data class PlanItem(
    var date: String?,
    var planID: Int?,
    var placeList: ArrayList<PlaceItem>?
) {
    constructor(): this(null, null, null)
}