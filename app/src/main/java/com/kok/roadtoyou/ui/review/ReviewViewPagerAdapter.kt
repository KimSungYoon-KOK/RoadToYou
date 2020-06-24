package com.kok.roadtoyou.ui.review

import android.animation.ObjectAnimator
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.kok.roadtoyou.ui.register_review.ReviewItem
import me.relex.circleindicator.CircleIndicator3
import java.util.*

class ReviewViewPagerAdapter (private val itemList:ArrayList<ReviewItem>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var context: Context

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = itemList[position]
        when(holder){
            is ViewHolder1 -> {
                val size = data.imgList!!.size - 1
                holder.review_img.adapter = ReviewImageViewPagerAdapter(context, (data.imgList as MutableList<String>).subList(0, size))
                val PageChangeCallback = object: ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        holder.img_num.text = (position + 1).toString() + "/$size"
                        super.onPageSelected(position)
                    }
                }
                holder.review_img.registerOnPageChangeCallback(PageChangeCallback)
                holder.previous.setOnClickListener {
                    holder.review_img.setCurrentItem(holder.review_img.currentItem - 1,true)
                }
                holder.next.setOnClickListener {
                    holder.review_img.setCurrentItem(holder.review_img.currentItem + 1,true)
                }

                var bnum = ""
                var num = data.num

                if(data.num < 10){
                    bnum = "0$num"+"º"
                } else{
                    bnum = "$num"+"º"
                }

                holder.review_num.text = bnum
                holder.review_name.text = data.upso_name


                var hashTag = ""
                // 수정
//                if(data.hashTag != null){
//                    for(i in 0 until data.hashTag.size){
//                        var temp = "#" + data.upso_hashTag[i] + " "
//                        hashTag += temp
//                    }
//                }

                holder.review_hashtag.text = hashTag
                holder.review_content.text = data.review_content
                holder.indicator.setViewPager(holder.review_img);

                // Bottom Sheet
                var mLastClickTime:Long = 0
                holder.more_info_btn.setOnClickListener {
                    // mis-clicking prevention, using threshold of 1000 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return@setOnClickListener
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    val bs = Review_BottomSheetDialog(c, data.posX, data.posY, data.location, data.call, data.like, data.dislike)
                    bs.getInstance()
                    bs.show(fm, "bottomSheet")

                }

                // relative
//                val move = MotionEvent()
//                holder.relative_layout.onInterceptTouchEvent()
            }
            is ViewHolder0 -> {
                var img = data.info!!.coverImg
//                holder.image.setImageResource(R.drawable.sample2)
                val url = "http://ksun1234.cafe24.com/" + img
                Log.d("dkdkdkdkkdkddkkdkdkd", url)
                Glide.with(c).load(url).thumbnail(0.1f).placeholder(R.drawable.no_image).into(holder.coverImg)
//                holder.coverImg.setImageDrawable(data.info!!.coverImg)

//                val animSlide = AnimationUtils.loadAnimation(c, R.anim.slide)
//                holder.coverImg.startAnimation(animSlide)

                holder.horizontal_scroll_view.post {
                    ObjectAnimator.ofInt(
                        holder.horizontal_scroll_view,
                        "scrollX",
                        300
                    ).setDuration(2000).start()
//                    holder.horizontal_scroll_view.smoothScrollTo(800,0)
                }

                holder.horizontal_scroll_view.setOnTouchListener { view, motionEvent ->
                    true
                }

                holder.title.text = data.info!!.title
                holder.date.text = data.info!!.date
                holder.userId.text = "ⓒ"+data.info!!.userId
            }
            is ViewHolder2 -> {

            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when(holder){
            is ViewHolder2 -> {
                holder.gMap.clear()
                holder.gMap.mapType = GoogleMap.MAP_TYPE_NONE
            }
            else -> {
                super.onViewRecycled(holder)
            }
        }
    }

    companion object {
        const val COVER = 0;
        const val CONTENT = 1;
        const val SUMMARY = 2;
    }
    override fun getItemViewType(position: Int): Int {
        return when(itemList[position].type) {
            0 -> COVER
            1 -> CONTENT
            2 -> SUMMARY
            else -> CONTENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            COVER -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_review_cover, parent, false)
                return ViewHolder0(v)
            }
            CONTENT -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_review_viewpager, parent, false)
                return ViewHolder1(v)
            }
            SUMMARY -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_review_summary, parent, false)
                return ViewHolder2(v)

            }
            else->{
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_review_viewpager, parent, false)
                return ViewHolder1(v)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder0(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title: TextView = itemView.findViewById(R.id.title)
        var date: TextView = itemView.findViewById(R.id.date)
        var coverImg: ImageView = itemView.findViewById(R.id.coverImg)
        var userId: TextView = itemView.findViewById(R.id.user_id)
        var horizontal_scroll_view: HorizontalScrollView = itemView.findViewById(R.id.horizontal_scroll_view)
    }
    // ViewHolder 0 -> Cover
    // ViewHolder 1 -> Content
    // ViewHolder 2 -> Summary
    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView){
        var review_img: ViewPager2
        var review_num: TextView
        var review_name: TextView
        var review_hashtag: TextView
        var review_content: TextView
        var more_info_btn: TextView
        var indicator: CircleIndicator3
        var img_num: TextView
        var previous: ImageButton
        var next: ImageButton
        var relative_layout: RelativeLayout
        init{
            review_img = itemView.findViewById(R.id.review_img)
            review_num = itemView.findViewById(R.id.review_num)
            review_name = itemView.findViewById(R.id.review_name)
            review_hashtag = itemView.findViewById(R.id.review_hashtag)
            review_content = itemView.findViewById(R.id.review_content)
            more_info_btn = itemView.findViewById(R.id.more_info_btn)
            indicator = itemView.findViewById(R.id.indicator)
            img_num = itemView.findViewById(R.id.img_num)
            previous = itemView.findViewById(R.id.previous)
            next = itemView.findViewById(R.id.next)
            relative_layout =  itemView.findViewById(R.id.relative_layout)

        }
    }
    inner class ViewHolder2(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
    {

        var selectedMarker: Marker? = null

        var map: SupportMapFragment
        var recyclerView:RecyclerView
        lateinit var gMap: GoogleMap
        init{
            recyclerView = itemView.findViewById(R.id.summary_recyclerView)

            // Map
            map = fm
                .findFragmentById(com.inseoul.R.id.summary_map) as SupportMapFragment
            map!!.getMapAsync(this)

            // recyclerView
            val real_item = itemList.subList(1,itemList.size-2)
            val adapter = ReviewSummaryAdapter(c, real_item)
            recyclerView.layoutManager = LinearLayoutManager(c)
            recyclerView.adapter = adapter


        }


        override fun onMarkerClick(p0: Marker?): Boolean {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            var center: CameraUpdate = CameraUpdateFactory.newLatLng(p0?.position)
            gMap.animateCamera(center)

            changeSelectedMarker(p0)

            return true
        }

        override fun onMapClick(p0: LatLng?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            changeSelectedMarker(null)
        }

        private fun addMarker(
            position: LatLng,
            //markerItem: AddPlaceItem,
            isSelectedMarker: Boolean
        ): Marker {


            var markerOptions = MarkerOptions()
            markerOptions.position(position!!)
//            markerOptions.title(order.toString())
//            markerOptions.snippet(order.toString())

            if (isSelectedMarker) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.click_marker))
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker))
            }

            return gMap.addMarker(markerOptions!!)
        }
        private fun addMarker(
            marker: Marker,
            isSelectedMarker: Boolean
        ): Marker {
            var title = marker.title
            var preview = marker.title
            var latlng = marker.position
            //var temp: AddPlaceItem = AddPlaceItem(title, preview, latlng)

            return addMarker(marker.position, isSelectedMarker)
        }
        fun changeSelectedMarker(marker: Marker?) {
            //선택했던 마커 되돌리기
            if (selectedMarker != null) {
                addMarker(selectedMarker!!, false)
                selectedMarker!!.remove()
            }

            //선택한 마커 표시
            if (marker != null) {
                selectedMarker = addMarker(marker, true)
                selectedMarker?.showInfoWindow()                        //정보창 띄우기
                marker.remove()
            }
        }

        override fun onMapReady(p0: GoogleMap?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            MapsInitializer.initialize(c)
            gMap = p0!!

//            var posX = 37.543492
//            var posY = 127.077388
//            var LatLng = LatLng(posX, posY)
//            var marker = MarkerOptions()
//            marker.position(LatLng)
//            marker.icon(BitmapDescriptorFactory.fromResource(com.inseoul.R.drawable.default_marker))
//            p0!!.addMarker(marker)
//            p0!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 13f))

            if(itemList[0].summary != null){
                var list = itemList[0].summary!!.list

                // Add PolyLine
                val POLYLINE_STROKE_WIDTH_PX = 7f
                val PATTERN_GAP_LENGTH_PX = 10f
                val DOT = Dot()
                val GAP = Gap(PATTERN_GAP_LENGTH_PX)
                val PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT)
                val polyline = p0.addPolyline(
                    PolylineOptions()
                        .clickable(true)
                        .addAll(list)
                )
                polyline.width = POLYLINE_STROKE_WIDTH_PX
                polyline.pattern = PATTERN_POLYLINE_DOTTED

                // Add Marker
                p0!!.moveCamera(CameraUpdateFactory.newLatLngZoom(list[0], 13f))
                for(i in 0 until list.size){
                    var marker = MarkerOptions()
                    marker.position(list[i])
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker))
                    p0.addMarker(marker)
                }

                p0.setOnMarkerClickListener(this)
                p0.setOnMapClickListener(this)
            }
        }

    }

}