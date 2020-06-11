package com.kok.roadtoyou.ui.addplan

import com.kok.roadtoyou.ui.search.PlaceItem

data class PlanItem(
    var planID: String?,
    var date: String?,
    var userId: List<String?>?,
    var placeList: List<PlaceItem>?
) {
    constructor(): this(null, null, null, null)
}