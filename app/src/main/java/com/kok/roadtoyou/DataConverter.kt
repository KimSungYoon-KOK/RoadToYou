package com.kok.roadtoyou

import com.kok.roadtoyou.ui.addplan.AddPlaceItem
import com.kok.roadtoyou.ui.addplan.PlanItem
import com.kok.roadtoyou.ui.member.User
import com.kok.roadtoyou.ui.mypage.MyItem
import java.time.LocalDate

class DataConverter {

    //PlanItem 으로 변형
    fun dataConvertPlanItem(json: String): PlanItem {

        val id_num = json.indexOf("planID")
        var temp = json.substring(id_num).indexOf(",")
        if (temp < 0) { temp = json.substring(id_num).indexOf("}") }
        var planID = json.substring(id_num, (temp+id_num))
        planID = planID.split("=")[1]

        val name_num = json.indexOf("planName")
        var planName = json.substring(name_num, (json.substring(name_num).indexOf(",")+name_num))
        planName = planName.split("=")[1]

        val p_num = json.indexOf("period")
        var period = json.substring(p_num, (json.substring(p_num).indexOf(",")+p_num))
        period = period.split("=")[1]

        val d_num = json.indexOf("days")
        var days_s = json.substring(d_num, (json.substring(d_num).indexOf(",")+d_num))
        days_s = days_s.split("=")[1]
        val days = days_s.toInt()

        val u_num = json.indexOf("userList")
        var userid = json.substring(u_num, (json.substring(u_num).indexOf("]")+u_num))
        userid = userid.split("=[")[1]
        val userId = userid.split(", ")

        val placeList = mutableListOf<AddPlaceItem>()
        val places = json.indexOf("placeList")
        if (places > 0) {
            var placeStr = json.substring(places, (json.substring(places).indexOf("}},")+places))
            placeStr = placeStr.split("placeList={")[1]
            val temp = placeStr.split("}, ").toMutableList()
            for (i in temp.indices) {
                temp[i] = temp[i].split("={")[1]
                val ttemp = temp[i].split(", ").toMutableList()
                for (j in ttemp.indices) {
                    ttemp[j] = ttemp[j].split("=")[1]
                }
                val item = AddPlaceItem(ttemp[0].toInt(), ttemp[1].toInt(), ttemp[2], ttemp[3].toInt())
                placeList.add(item)
            }
        }

        return PlanItem(planID, planName, period, days, userId, placeList)
    }

    //PlanItem 에서 MyItem 으로 변형
    fun dataConvertMyItemFromPlan(json: String): MyItem {
        var planID = "planID="
        val id_num = json.indexOf(planID)
        var temp = json.substring(id_num).indexOf(",")
        if (temp < 0) { temp = json.substring(id_num).indexOf("}") }
        planID = json.substring(id_num, (temp+id_num))
        planID = planID.split("=")[1]

        var planName = "planName="
        val name_num = json.indexOf(planName)
        planName = json.substring(name_num, (json.substring(name_num).indexOf(",")+name_num))
        planName = planName.split("=")[1]

        var period = "period="
        val p_num = json.indexOf(period)
        period = json.substring(p_num, (json.substring(p_num).indexOf(",")+p_num))
        period = period.split("=")[1]

        return MyItem(planID, planName, period)
    }

    //ReviewItem 에서 ReviewItem 으로 변형
    fun dataConvertMyItemFromReview(json: String): MyItem {
        //TODO:DATA CONVERT
    }

    //User 로 변형
    fun dataConverterUser(json: String): User {
        var uid = "uid="
        val uid_num = json.indexOf(uid)
        uid = json.substring(uid_num, (json.substring(uid_num).indexOf(",")+uid_num))
        uid = uid.split("=")[1]

        var name = "name="
        val name_num = json.indexOf(name)
        var temp = json.substring(name_num).indexOf(",")
        if (temp < 0) { temp = json.substring(name_num).indexOf("}") }
        name = json.substring(name_num, (temp+name_num))

        name = name.split("=")[1]

        val planList = mutableListOf<String>()
        var planStr = "planList={"
        val plan_num = json.indexOf(planStr)
        if (plan_num != -1) {
            planStr = json.substring(plan_num, (json.substring(plan_num).indexOf("}") + plan_num))
            planStr = planStr.split("={")[1]
            val plans = planStr.split(", ")
            for (i in plans.indices) {
                planList.add(plans[i].split("=")[0])
            }
        }

        val reviewList = mutableListOf<String>()
        var reviewStr = "reviewList={"
        val review_num = json.indexOf(reviewStr)
        if (review_num != -1) {
            reviewStr = json.substring(review_num, (json.substring(review_num).indexOf("}")+review_num))
            reviewStr = reviewStr.split("={")[1]
            val reviews = reviewStr.split(", ")
            for (i in reviews.indices) {
                reviewList.add(reviews[i].split("=")[0])
            }
        }

        return User(uid, name, planList, reviewList)
    }

    fun dateCalculate(period: String): Boolean {
        val now = LocalDate.now()
        val temp = period.split(" - ")
        val endDate = temp[1].split(".")
        val date = if (endDate.size >= 3) {
            LocalDate.of(endDate[0].toInt(), endDate[1].toInt(), endDate[2].toInt())
        } else {
            val year = temp[0].split(".")[0]
            LocalDate.of(year.toInt(), endDate[0].toInt(), endDate[1].toInt())
        }

        return now <= date
    }
}