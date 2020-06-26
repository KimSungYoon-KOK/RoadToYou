package com.kok.roadtoyou.ui.review

import android.animation.ObjectAnimator
import android.content.Context
import android.os.AsyncTask
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.register_review.ReviewItem
import me.relex.circleindicator.CircleIndicator3
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.lang.ref.WeakReference
import java.net.URL
import java.util.zip.DeflaterOutputStream


class ReviewViewPagerAdapter (
    private val fragmentManager: FragmentManager,
    private val reviewName:String,
    private val reviewPeriod:String,
    private val coverImg:String,
    private val userId: String,
    private val itemList:MutableList<ReviewItem>
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var context: Context
    private val key = "V3TPLc8KikVyK235xNyOorabnl1eDnekQJSTWtpl4eQXyE3MWxAUjlZXJo6PIxrmLZGlixdOVWTSs8PmCfb4nQ%3D%3D"
    private val suffix = "&addrinfoYN=Y&mapinfoYN=Y&MobileOS=AND&MobileApp=roadtoyou"

    companion object {
        const val COVER = 0
        const val CONTENT = 1
    }

    // ViewHolder 0 -> Cover
    // ViewHolder 1 -> Content
    inner class ViewHolder0(itemView: View): RecyclerView.ViewHolder(itemView){
        var title: TextView = itemView.findViewById(R.id.title)
        var date: TextView = itemView.findViewById(R.id.date)
        var coverImageView: ImageView = itemView.findViewById(R.id.imageView_coverImg)
        var userId: TextView = itemView.findViewById(R.id.user_id)
        var horizontalScrollView: HorizontalScrollView = itemView.findViewById(R.id.horizontal_scroll_view)
    }

    inner class ViewHolder1(itemView: View): RecyclerView.ViewHolder(itemView){
        var review_img: ViewPager2 = itemView.findViewById(R.id.review_img)
        var review_name: TextView = itemView.findViewById(R.id.review_name)
        var review_hashtag: TextView = itemView.findViewById(R.id.review_hashtag)
        var review_content: TextView = itemView.findViewById(R.id.review_content)
        var more_info_btn: TextView = itemView.findViewById(R.id.more_info_btn)
        var indicator: CircleIndicator3 = itemView.findViewById(R.id.indicator)
        var img_num: TextView = itemView.findViewById(R.id.img_num)
        var previous: ImageButton = itemView.findViewById(R.id.previous)
        var next: ImageButton = itemView.findViewById(R.id.next)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when(viewType){
            COVER -> {
                val v = LayoutInflater.from(context).inflate(R.layout.viewpager_review_cover, parent, false)
                ViewHolder0(v)
            }
            CONTENT -> {
                val v = LayoutInflater.from(context).inflate(R.layout.viewpager_review_content, parent, false)
                ViewHolder1(v)
            }
            else->{
                val v = LayoutInflater.from(context).inflate(R.layout.viewpager_review_content, parent, false)
                ViewHolder1(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = itemList[position]
        when(holder){
            is ViewHolder0 -> {
                Glide.with(context).load(coverImg).thumbnail(0.1f).placeholder(R.drawable.ic_baseline_error_outline_24).into(holder.coverImageView)
                holder.horizontalScrollView.post {
                    ObjectAnimator.ofInt(
                        holder.horizontalScrollView,
                        "scrollX",
                        300
                    ).setDuration(2000).start()
                }

                holder.horizontalScrollView.setOnTouchListener { _, _ -> true }
                holder.title.text = reviewName
                holder.date.text = reviewPeriod
                holder.userId.text = "ⓒ${userId}"
            }
            is ViewHolder1 -> {
                holder.review_img.adapter = ReviewImageViewPagerAdapter(data.imgList!!)
                val pageChangeCallback = object: ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        holder.img_num.text = "${position + 1}/${data.imgList!!.size}"
                        super.onPageSelected(position)
                    }
                }
                holder.review_img.registerOnPageChangeCallback(pageChangeCallback)
                holder.previous.setOnClickListener {
                    holder.review_img.setCurrentItem(holder.review_img.currentItem - 1,true)
                }
                holder.next.setOnClickListener {
                    holder.review_img.setCurrentItem(holder.review_img.currentItem + 1,true)
                }
                holder.review_name.text = data.placeName

                var hashTagStr = ""
                if(data.hashTags != null){
                    for(i in data.hashTags!!.indices){
                        if (i > 4) {
                            hashTagStr += "..."
                            break
                        }
                        val temp = "#" + data.hashTags!![i] + " "
                        hashTagStr += temp
                    }
                }

                holder.review_hashtag.text = hashTagStr
                holder.review_content.text = data.review!!.split("^^&**!@")[0]
                holder.indicator.setViewPager(holder.review_img)

                // Bottom Sheet
                var mLastClickTime:Long = 0
                holder.more_info_btn.setOnClickListener {
                    // mis-clicking prevention, using threshold of 1000 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return@setOnClickListener
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()
                    infoTask(data.placeId)
                }
            }


        }
    }

    //////////////////////////// 장소 주소 정보 받아오기 ////////////////////////////////
    private fun infoTask(placeID: Int?) {
        val task = InfoAsyncTask(this)
        if (placeID == -1) return
        task.execute(URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=$key&contentId=$placeID$suffix"))
    }

    private class InfoAsyncTask(adapter: ReviewViewPagerAdapter): AsyncTask<URL, Unit, PlaceInfo>() {

        private val adapterReference = WeakReference(adapter)

        override fun doInBackground(vararg params: URL?): PlaceInfo {
            val doc = Jsoup.connect(params[0].toString()).parser(Parser.xmlParser()).get()

            val lat = doc.select("mapy").text().toDouble()
            val lng = doc.select("mapx").text().toDouble()
            val location = doc.select("addr1").text()

            return PlaceInfo(lat, lng, location)
        }

        override fun onPostExecute(result: PlaceInfo) {
            super.onPostExecute(result)
            val mAdapter = adapterReference.get()

            val bs = ReviewBottomSheetDialog(result.lat, result.lng, result.location)
            bs.getInstance()
            bs.show(mAdapter!!.fragmentManager, "bottomSheet")
        }

    }


    override fun getItemViewType(position: Int): Int {
        return when(itemList[position].viewType) {
            0 -> COVER
            else -> CONTENT
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}