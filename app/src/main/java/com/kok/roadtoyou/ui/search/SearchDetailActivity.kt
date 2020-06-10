package com.kok.roadtoyou.ui.search

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_search_detail.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLEncoder
import kotlin.properties.Delegates

class SearchDetailActivity : AppCompatActivity() {

    private val key = "V3TPLc8KikVyK235xNyOorabnl1eDnekQJSTWtpl4eQXyE3MWxAUjlZXJo6PIxrmLZGlixdOVWTSs8PmCfb4nQ%3D%3D"
    private val suffix = "&imageYN=Y&MobileOS=AND&MobileApp=roadtoyou"

    var imgList = ArrayList<String>()
    lateinit var placeInfo: Places
    lateinit var adapter: SearchDetailViewPagerAdapter

    lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_detail)
        init()
    }

    private fun init() {
        //이미지 뷰페이저 어댑터
        adapter = SearchDetailViewPagerAdapter(imgList)
        viewpager_detail_image.adapter = adapter
        indicator.setViewPager(viewpager_detail_image)

        //이미지 이전, 다음 버튼
        previous.setOnClickListener {
            viewpager_detail_image.setCurrentItem(viewpager_detail_image.currentItem - 1, true)
        }
        next.setOnClickListener {
            viewpager_detail_image.setCurrentItem(viewpager_detail_image.currentItem + 1, true)
        }

        initData()
        initMap(LatLng(placeInfo.posX!!, placeInfo.posY!!))
    }

    private fun initData() {
        val flag = intent.getBooleanExtra("FLAG", false)
        placeInfo = intent.getParcelableExtra("PLACE_DATA")

        if (flag) {
            // 시작 액티비티가 SearchActivity == false, AddPlaceActivity == true
        }

        imgTask(placeInfo.id)        //Image URL Parsing
        // TODO: 세부 정보 데이터 입력
        initView()
    }

    private fun initView() {
        detail_title.text = placeInfo.title
        detail_address.text = "주소: " + placeInfo.addr1

        if(placeInfo.tel == "02-120" || placeInfo.tel == "전환번호 미등록" || placeInfo.tel == "none"){
            detail_tel.text = "전화 번호 미등록..."
        } else {
            detail_tel.text = "전화 번호: " + placeInfo.tel
        }

        when(placeInfo.type){
            39 -> detail_type.append("#맛집 ")
            12-> detail_type.append("#명소 ")
            32 -> detail_type.append("#숙소 ")
            else -> detail_type.append("#문화 ")
        }
    }

    private fun initMap(loc: LatLng) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_detail) as SupportMapFragment
        mapFragment.getMapAsync{
            googleMap = it
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f))
            googleMap.setMinZoomPreference(10.0f)       //최소 줌
            googleMap.setMaxZoomPreference(18.0f)       //최대 줌

            //Marker
            val options = MarkerOptions()
            options.position(loc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                .title("역")
//                .snippet("서울역")
            val mk1 = googleMap.addMarker(options)
            mk1.showInfoWindow()
        }
    }

    ////////////////////////////////////// API Image Parsing ///////////////////////////////////////
    private fun imgTask(placeID: Int?) {
        val task = ImgAsyncTask(this)
        if (placeID == -1) return
        task.execute(URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage?ServiceKey=$key&contentId=$placeID&$suffix"))
    }

    class ImgAsyncTask(context: SearchDetailActivity): AsyncTask<URL, Unit, Unit>() {

        val activityreference = WeakReference(context)

        override fun doInBackground(vararg params: URL?): Unit {
            val activity = activityreference.get()
            activity?.adapter?.items?.clear()

            val doc = Jsoup.connect(params[0].toString()).parser(Parser.xmlParser()).get()
            val items = doc.select("item")
            //val totalCount = doc.select("totalCount").text().toString().toInt()
            for (item in items) {
                val imgUrl = item.select("originimgurl").text().toString()
                //Log.d("IMG_URL", imgUrl)
                activity?.adapter!!.items.add(imgUrl)
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityreference.get()
            if (activity == null || activity.isFinishing)
                return
            activity.adapter.notifyDataSetChanged()
        }
    }
}