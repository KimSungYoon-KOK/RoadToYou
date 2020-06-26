package com.kok.roadtoyou.ui.addplan

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kok.roadtoyou.BackPressCloseHandler
import com.kok.roadtoyou.DataConverter
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.search.PlaceItem
import com.kok.roadtoyou.ui.search.SearchActivity
import kotlinx.android.synthetic.main.activity_make_plan.*
import java.util.*
import kotlin.collections.ArrayList

class MakePlanActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    lateinit var mMap: GoogleMap
    private var selectedMarker: Marker? = null

    lateinit var adapter: MakePlanViewPagerAdapter
    lateinit var planItem: PlanItem
    private var itemList = ArrayList<ArrayList<AddPlaceItem>>()

    lateinit var plansDB: DatabaseReference
    lateinit var userDB: DatabaseReference

    private lateinit var fade_out: Animation
    private lateinit var fade_in: Animation
    private lateinit var rotate_open: Animation
    private lateinit var rotate_close: Animation
    var isBtnOpen: Boolean = false

    private val POLYLINE_STROKE_WIDTH_PX = 7f
    private val PATTERN_GAP_LENGTH_PX = 10f
    private val DOT = Dot()
    private val GAP = Gap(PATTERN_GAP_LENGTH_PX)
    private val PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_plan)
        initToolbar()
        init()
    }

    private fun init() {
        initMap()

        val activityFlag = intent.getIntExtra("ACTIVITY_FLAG", -1)
        if (activityFlag == 1) {
            //from AddPlanFragment
            planItem = intent.getParcelableExtra("PLAN_ITEM")!!
            initView()
        } else {
            //from MyPageFragment or HomeFragment
            initPlan(activityFlag)
        }
    }


    private fun initPlan(activityFlag: Int) {
        val planID = intent.getStringExtra("PLAN_ID")
        plansDB = FirebaseDatabase.getInstance().getReference("plans")
        plansDB.orderByKey().equalTo(planID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("Log_Plan_Item",p0.value.toString())
                planItem = DataConverter().dataConvertPlanItem(p0.value.toString())
                Log.d("Log_Plan_Item",planItem.toString())
                if (activityFlag == 2) {
                    initClipping()
                }
                initView()
            }
        })
    }

    private fun initClipping() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        userDB = FirebaseDatabase.getInstance().getReference("users/${user.uid}")
        val key = userDB.child("planList").push().key!!
        userDB.child("planList/${key}").setValue(planItem.planName)
        plansDB = FirebaseDatabase.getInstance().getReference("plans")
        val item = PlanItem(
            key,
            planItem.planName,
            planItem.period,
            planItem.days,
            listOf(user.uid),
            planItem.placeList
        )
        Log.d("Log_Plan_Info", item.toString())
        plansDB.child(key).setValue(item)
    }

    private fun initBtn() {
        //장소 추가 버튼
        addPlaceBtn.setOnClickListener {
            if (itemList[viewpager_make_plan.currentItem].size >= 50) {
                Toast.makeText(this, "각 날짜의 일정은 50개를 넘길 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("FLAG", true)
            startActivityForResult(intent, 3000)
        }

        //메모 추가 버튼
        addMemoBtn.setOnClickListener {
            //TODO: memo
        }

        finishBtn.setOnClickListener {
            val planId = planItem.planID
            plansDB.child("$planId/planName").setValue(et_planTitle.text.toString())
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        fade_out = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out_morebtn_anim)
        rotate_open = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_anim)
        rotate_close = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_anim_close)
        fade_in = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in_btn_anim)

        edit_more.setOnClickListener {
            anim()
        }

        editPlanDateBtn.setOnClickListener {
            anim()
            val intent = Intent(this, EditDateActivity::class.java)
            startActivityForResult(intent, 2000)
        }


        deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("등록 된 장소들이 삭제됩니다.\n일정을 삭제하시겠습니까?")
                .setPositiveButton("확인") { _, _ ->
                    anim()
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val planId = planItem.planID!!
                        plansDB.child(planId).removeValue()

                        userDB = FirebaseDatabase.getInstance().getReference("users/${user.uid}")
                        userDB.child("planList/${planId}").removeValue()
                        finish()
                    } else {
                        Toast.makeText(this, "다시 한번 시도 해주세요.", Toast.LENGTH_SHORT).show()
                    }

                }
                .setNegativeButton("취소") { _, _ -> anim() }
                .create().show()
        }
    }

    private fun initView() {
        initBtn()
        tv_plan_date.text = planItem.period
        et_planTitle.setText(planItem.planName)

        itemList.clear()
        for (i in 0 until planItem.days!!) {
            val temp = ArrayList<AddPlaceItem>()
            itemList.add(temp)
        }
        val placeList = planItem.placeList?.sortedWith(compareBy{ it.count })
        if (placeList != null) {
            for (i in placeList.indices) {
                itemList[placeList[i].date!!].add(placeList[i])
            }
        }
        val listener = object : MakePlanViewPagerAdapter.ViewPagerAdapterEventListener {
            override fun onChangeCallback2(
                view: View,
                itemlist: ArrayList<ArrayList<AddPlaceItem>>
            ) {
                itemList = itemlist
                initMarker()
            }
        }
        adapter = MakePlanViewPagerAdapter(listener, itemList)
        viewpager_make_plan.adapter = adapter

        TabLayoutMediator(tabLayout_make_plan, viewpager_make_plan) { tab, position ->
            tab.text =  "DAY ${position+1}"
        }.attach()

        //TabLayout Mode 변경
        if (tabLayout_make_plan.tabCount > 6)
            tabLayout_make_plan.tabMode = TabLayout.MODE_SCROLLABLE
    }

    private fun initMap() {
        val defaultLoc = LatLng(36.38, 127.51)     //남한 중심 좌표 - 괴산군
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{
            mMap = it
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 9f))
            mMap.setMinZoomPreference(6.0f)       //최소 줌
            mMap.setMaxZoomPreference(18.0f)      //최대 줌
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                //SearchActivity 에서 "선택"버튼으로 넘어왔을 때
                3000 -> uploadData(data)
                2000 -> editData(data)
            }
        }
    }

    //Firebase PlaceList 에 추가
    private fun uploadData(data: Intent?) {
        if (data != null) {
            val placeItem = data.getParcelableExtra<PlaceItem>("PLACE_DATA") ?: return
            val selectDate = viewpager_make_plan.currentItem
            val tempItem = AddPlaceItem(
                selectDate,
                itemList[selectDate].size +1,
                placeItem.title,
                placeItem.id,
                placeItem.type,
                "${placeItem.lat}&${placeItem.lng}"
            )
            plansDB = FirebaseDatabase.getInstance().getReference("plans/${planItem.planID}")
            plansDB.child("placeList/${placeItem.id}").setValue(tempItem)
            itemList[selectDate].add(tempItem)
            adapter.notifyDataSetChanged()
            initMarker()
        } else {
            Log.e("Error", "Data Intent is null")
        }
    }

    private fun editData(data: Intent?) {
        if (data != null) {
            val period = data.getStringExtra("EDIT_PERIOD")
            val days = data.getIntExtra("EDIT_DAYS", 1)
            Log.d("Log_period", period)
            if (period != null) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val planId = planItem.planID!!
                    plansDB.child("$planId/period").setValue(period)
                    plansDB.child("$planId/days").setValue(days)
                    userDB = FirebaseDatabase.getInstance().getReference("users/${user.uid}")
                    userDB.child("planList/${planId}").setValue(period)

                    planItem.period = period
                    planItem.days = days
                    initView()
                } else {
                    Toast.makeText(this, "다시 한번 시도 해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            initMarker()
        } else {
            Log.e("Error", "Data Intent is null")
        }
    }



    /////////////////////////////// Init Marker ////////////////////////////////////
    fun initMarker() {
        mMap.clear()
        val markerList = itemList[viewpager_make_plan.currentItem]
        for (i in 0 until markerList.size) {
            addMarker(markerList[i], false)
        }
        getMarkerItems()
        if (markerList.size != 0) {
            val location = itemList[viewpager_make_plan.currentItem][0].latLng!!
            val temp = location.split("&")
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(temp[0].toDouble(), temp[1].toDouble()), 12f))
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.552122, 126.988270), 12f))
        }
    }

    private fun getMarkerItems() {
        val lineList = ArrayList<LatLng>()
        for (i in itemList[viewpager_make_plan.currentItem].indices) {
            val location = itemList[viewpager_make_plan.currentItem][0].latLng!!
            val temp = location.split("&")
            lineList.add(LatLng(temp[0].toDouble(), temp[1].toDouble()))
        }

        val polyline = mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(lineList)
        )
        polyline.width = POLYLINE_STROKE_WIDTH_PX
        polyline.pattern = PATTERN_POLYLINE_DOTTED
    }

    private fun addMarker(
        markerItem: AddPlaceItem,
        isSelectedMarker: Boolean
    ): Marker {

        val placeNm = markerItem.title
        val location = markerItem.latLng!!
        val temp = location.split("&")
        val placePosition = LatLng(temp[0].toDouble(), temp[1].toDouble())
        val mCount = markerItem.count

        val markerOptions = MarkerOptions()

        markerOptions.position(placePosition)
        markerOptions.title(placeNm)
        markerOptions.snippet(mCount.toString() + " 번째 일정")

        if (isSelectedMarker) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.click_marker))
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker))
        }

        return mMap.addMarker(markerOptions)
    }

    private fun addMarker(
        marker: Marker,
        isSelectedMarker: Boolean
    ): Marker {
        val count = marker.snippet.split(" ")[0].toInt()
        val selectDate = itemList[viewpager_make_plan.currentItem][count - 1].date
        val markerStr = "${marker.position.latitude}&${marker.position.longitude}"
        val temp = AddPlaceItem(selectDate, count, marker.title, null, null, markerStr)
        return addMarker(temp, isSelectedMarker)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        CameraUpdateFactory.newLatLng(marker?.position)
        changeSelectedMarker(marker)
        return true
    }

    private fun changeSelectedMarker(marker: Marker?) {
        //선택했던 마커 되돌리기
        if (selectedMarker != null) {
            addMarker(selectedMarker!!, false)
            selectedMarker!!.remove()
        }

        //선택한 마커 표시
        if (marker != null) {
            selectedMarker = addMarker(marker, true)
            selectedMarker?.showInfoWindow()  //정보창 띄우기
            marker.remove()
        }
    }


    private fun anim() {
        if (isBtnOpen) {
            editPlanDateBtn.visibility = View.INVISIBLE
            deleteBtn.visibility = View.INVISIBLE
            edit_more.startAnimation(rotate_close)
            editPlanDateBtn.startAnimation(fade_out)
            deleteBtn.startAnimation(fade_out)
            editPlanDateBtn.isClickable = false
            deleteBtn.isClickable = false
            isBtnOpen = false
        } else {
            edit_more.startAnimation(rotate_open)
            editPlanDateBtn.startAnimation(fade_in)
            deleteBtn.startAnimation(fade_in)
            editPlanDateBtn.isClickable = true
            deleteBtn.isClickable = true
            isBtnOpen = true
        }
    }


    ////////////////Toolbar//////////////
    fun initToolbar() {
        setSupportActionBar(toolbar_add_place)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

