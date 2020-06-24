package com.kok.roadtoyou

import com.kok.roadtoyou.ui.addplan.AddPlaceItem
import com.kok.roadtoyou.ui.addplan.PlanItem
import com.kok.roadtoyou.ui.member.User
import com.kok.roadtoyou.ui.mypage.MyItem
import com.kok.roadtoyou.ui.register_review.ReviewInfo
import com.kok.roadtoyou.ui.register_review.ReviewItem
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
                val item = AddPlaceItem(ttemp[0].toInt(), ttemp[1].toInt(), ttemp[3], ttemp[2].toInt(), ttemp[4].toInt())
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
        val r_num = json.indexOf("reviewId=")
        var reviewId = json.substring(r_num, (json.substring(r_num).indexOf(", ")+r_num))
        reviewId = reviewId.split("=")[1]

        var reviewName = "reviewName="
        val name_num = json.indexOf(reviewName)
        reviewName = json.substring(name_num, (json.substring(name_num).indexOf(",")+name_num))
        reviewName = reviewName.split("=")[1]

        var period = "period="
        val p_num = json.indexOf(period)
        period = json.substring(p_num, (json.substring(p_num).indexOf(",")+p_num))
        period = period.split("=")[1]

        return MyItem(reviewId, reviewName, period)
    }

    fun dataConverterReviewInfo(json: String): ReviewInfo {
        val name_num = json.indexOf("reviewName")
        var reviewName = json.substring(name_num, (json.substring(name_num).indexOf(",")+name_num))
        reviewName = reviewName.split("=")[1]

        val p_num = json.indexOf("period")
        var period = json.substring(p_num, (json.substring(p_num).indexOf(",")+p_num))
        period = period.split("=")[1]

        val rid_num = json.indexOf("reviewId")
        var reviewId = json.substring(rid_num, (json.substring(rid_num).indexOf(",")+rid_num))
        reviewId = reviewId.split("=")[1]

        val img_num = json.indexOf("coverImg")
        var coverImg = json.substring(img_num, (json.substring(img_num).indexOf(",")+img_num))
        coverImg = coverImg.split("=")[1]

        val uid_num = json.indexOf("userId")
        var userId = json.substring(uid_num, (json.substring(uid_num).indexOf("}")+uid_num))
        userId = userId.split("=")[1]

        val reviewList = mutableListOf<ReviewItem>()
        val list_num = json.indexOf("reviewList")
        var tempList = json.substring(list_num, (json.substring(list_num).indexOf("}],")+list_num))
        tempList = tempList.split("reviewList=[")[1]
        val list = tempList.split("},").toMutableList()
        for (i in list.indices) {
            list[i] += "}"
            reviewList.add(reviewItemConverter(list[i], i))
        }


        return ReviewInfo(reviewName, period, reviewId, coverImg, userId, reviewList)
    }

    fun reviewItemConverter(json: String, index: Int): ReviewItem {

        val pname_num = json.indexOf("placeName")
        var placeName:String
        if (json.substring(pname_num).indexOf(",") != -1) {
            placeName = json.substring(pname_num, (json.substring(pname_num).indexOf(",")+pname_num))
            placeName = placeName.split("=")[1]
        } else {
            placeName = json.substring(pname_num, (json.substring(pname_num).indexOf("}")+pname_num))
            placeName = placeName.split("=")[1]
        }


        val pid_num = json.indexOf("placeId")
        var placeId = ""
        if (json.substring(pid_num).indexOf(",") == -1) {
            placeId = json.substring(pid_num, (json.substring(pid_num).indexOf("}")+pid_num))
            placeId = placeId.split("=")[1]
        } else {
            placeId = json.substring(pid_num, (json.substring(pid_num).indexOf(",")+pid_num))
            placeId = placeId.split("=")[1]
        }


        var review: String?
        val str_num = json.indexOf("review=")
        if (str_num != -1) {
            review = json.substring(str_num, (json.substring(str_num).indexOf("^^&**!@,")+str_num))
            review = review.split("=")[1]
        } else {
            review = null
        }


        val hashTags: List<String>?
        val tag_num = json.indexOf("hashTags")
        if (tag_num != -1) {
            var tags = json.substring(tag_num, (json.substring(tag_num).indexOf("], ")+tag_num))
            tags = tags.split("=[")[1]
            hashTags = tags.split(", ")
        } else {
            hashTags = null
        }

        val imgList = mutableListOf<String>()
        val img_num = json.indexOf("imgList=[")
        if (img_num != -1) {
            var imgs = json.substring(img_num, (json.substring(img_num).indexOf("]")+img_num))
            imgs = imgs.split("=[")[1]
            val temp = imgs.split(", ")
            for (i in temp.indices) {
                imgList.add(temp[i])
            }
        }

        return if (index == 0) {
            ReviewItem(0, placeName, placeId.toInt(), review, hashTags, imgList)
        } else {
            ReviewItem(1, placeName, placeId.toInt(), review, hashTags, imgList)
        }
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