package com.kok.roadtoyou.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kok.roadtoyou.DataConverter
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.member.User
import kotlinx.android.synthetic.main.fragment_my_page.*

class MyPageFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var planDB: DatabaseReference
    private lateinit var reviewDB: DatabaseReference
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

    private fun initPlanList() {
        val planIdList = userInfo.planList
        if (planIdList != null) {
            for (i in planIdList.indices) {
                planDB = FirebaseDatabase.getInstance().getReference("plans/${planIdList[i]}")
                planDB.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
//                        TODO("Not yet implemented")
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("Log_Plan_List_$i", p0.value.toString())
                        if (p0.value != null) {
                            val item = DataConverter().dataConvertMyItemFromPlan(p0.value.toString())
                            Log.d("Log_Plan_Item_$i", item.toString())
                            if (DataConverter().dateCalculate(item.period!!)) itemList[0].add(item)
                            else itemList[1].add(item)
                        }
                        adapter.notifyDataSetChanged()
                    }
                })
            }
        }
    }

    private fun initReviewList() {
        val reviewIdList = userInfo.reviewList
        if (reviewIdList != null) {
            for (i in reviewIdList.indices) {
                reviewDB = FirebaseDatabase.getInstance().getReference("reviews/${reviewIdList[i]}")
                reviewDB.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
//                        TODO("Not yet implemented")
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("Log_Review_List_$i", p0.value.toString())
                        val item = DataConverter().dataConvertMyItemFromReview(p0.value.toString())
                        Log.d("Log_Review_Item_$i", item.toString())
                        itemList[2].add(item)
                        adapter.notifyDataSetChanged()
                    }
                })
            }
        }
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
                    userInfo = DataConverter().dataConverterUser(p0.value.toString())
                    Log.d("Log_User_Info2", userInfo.toString())

                    //user 프로필 사진
                    Glide.with(activity!!).load(user.photoUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .into(imageView_userPhoto)
                    //User name
                    tv_userName.text = userInfo.name

                    initPlanList()
                    initReviewList()
                    initViewPager()
                }
            })
        }
    }
}