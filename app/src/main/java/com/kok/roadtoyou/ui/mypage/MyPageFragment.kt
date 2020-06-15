package com.kok.roadtoyou.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.addplan.AddPlaceItem
import com.kok.roadtoyou.ui.addplan.PlanItem
import com.kok.roadtoyou.ui.member.User
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import java.time.LocalDate

class MyPageFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    lateinit var userInfo: User

    lateinit var adapter: MyPageViewPagerAdapter
    var itemList = ArrayList<ArrayList<MyItem>>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUserInfo()
    }

    private fun initViewPager(){
        repeat(3) {
            val temp = ArrayList<MyItem>()
            itemList.add(temp)
        }
        adapter = MyPageViewPagerAdapter(itemList)
        viewPager_my_page.adapter = adapter

        val tabLayoutTextArray = arrayListOf("내 여행", "지난 여행", "리뷰")
        TabLayoutMediator(tabLayout_my_page, viewPager_my_page) { tab, position ->
            tab.text = "${tabLayoutTextArray[position]} ${itemList[position].size}"
        }.attach()
    }

    private fun initItemList() {
        val planIdList = userInfo.planList
        if (planIdList != null) {
            for (i in planIdList.indices) {
                mDatabase = FirebaseDatabase.getInstance().getReference("plans/${planIdList[i]}")
                mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
//                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        //Log.d("Log_Plan_List_$i", p0.value.toString())
                        val item = dataConverter_Plan(p0.value.toString())
                        Log.d("Log_Plan_Item_$i", item.toString())
                        if (dateCalculate(item.period!!)) itemList[0].add(item)
                        else itemList[1].add(item)
                        adapter.notifyDataSetChanged()
                    }
                })
            }
        }
    }

    private fun dateCalculate(period: String): Boolean {
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

    private fun initUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user !== null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("users/${user.uid}")

            //User 정보
            mDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
//                    TODO("Not yet implemented")
                }
                override fun onDataChange(p0: DataSnapshot) {
                    //Log.d("Log_User_Info1",p0.value.toString())
                    userInfo = dataConverter_User(p0.value.toString())
                    Log.d("Log_User_Info2", userInfo.toString())

                    //user 프로필 사진
                    Glide.with(activity!!).load(user.photoUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .into(imageView_userPhoto)
                    //User name
                    tv_userName.text = userInfo.name

                    initItemList()
                    initViewPager()
                }
            })
        }
    }

    private fun dataConverter_Plan(json: String): MyItem {
        var planID = "planID="
        val id_num = json.indexOf(planID)
        planID = json.substring(id_num, (json.substring(id_num).indexOf(",")+id_num))
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

    //Database 에서 json 받아와서 User 객체에 다시 저장하는 함수
    private fun dataConverter_User(json: String): User {
        var uid = "uid="
        val uid_num = json.indexOf(uid)
        uid = json.substring(uid_num, (json.substring(uid_num).indexOf(",")+uid_num))
        uid = uid.split("=")[1]

        var name = "name="
        val name_num = json.indexOf(name)
        name = json.substring(name_num, (json.substring(name_num).indexOf(",")+name_num))
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

}