package com.kok.roadtoyou.ui.member

import com.kok.roadtoyou.ui.addplan.PlanItem

data class User(val uId: String?, val name: String?, val planId: List<PlanItem>?){
    constructor(): this(null, null, null)
}