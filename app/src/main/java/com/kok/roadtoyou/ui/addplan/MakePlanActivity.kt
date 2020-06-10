package com.kok.roadtoyou.ui.addplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kok.roadtoyou.R

class MakePlanActivity : AppCompatActivity() {

    lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_plan)
        init()
    }
    private fun init() {
        initMap()

        val flag = intent.getIntExtra("FLAG_KEY", -1)
        val startDate = intent.getStringExtra("START_DATE")
        val endDate = intent.getStringExtra("END_DATE")

//        intent.putExtra("PLAN_DATE", resultStr)
//        intent.putExtra("PLAN_RANGE", days.size)
        if (flag == 1){
            //from AddPlanFragment
        } else {
            //from MyPageFragment
        }
    }

    private fun initMap() {
        val defaultLoc = LatLng(36.38, 127.51)     //남한 중심 좌표 - 괴산군
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{
            mMap = it
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 9f))
            mMap.setMinZoomPreference(6.0f)       //최소 줌
            mMap.setMaxZoomPreference(18.0f)       //최대 줌
        }
    }
}