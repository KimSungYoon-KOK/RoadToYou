package com.kok.roadtoyou.ui.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import com.kok.roadtoyou.DataConverter
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.register_review.ReviewInfo
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity() {

    lateinit var reviewDB: DatabaseReference
    lateinit var reviewInfo: ReviewInfo

    lateinit var adapter: ReviewViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        initData()
    }

    private fun initView() {
        val toast = Toast.makeText(this, "좌우로 드래그 하세요", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 50)
        toast.show()

        adapter = ReviewViewPagerAdapter(supportFragmentManager, reviewInfo.reviewName, reviewInfo.period, reviewInfo.coverImg, reviewInfo.userId, reviewInfo.reviewList!!)
        viewpager_review.adapter = adapter
        val pageChangeCallback = object: ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                when(state){
                    ViewPager2.SCROLL_STATE_IDLE->{

                    }
                    ViewPager2.SCROLL_STATE_SETTLING->{

                    }
                    ViewPager2.SCROLL_STATE_DRAGGING->{

                    }
                }
                super.onPageScrollStateChanged(state)
            }
        }
        viewpager_review.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun initData() {
        val reviewId = intent.getStringExtra("REVIEW_ID") ?: return
        reviewDB = FirebaseDatabase.getInstance().getReference("reviews/$reviewId")
        reviewDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
//                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("Log_Review_Info", p0.value.toString())
                reviewInfo = DataConverter().dataConverterReviewInfo(p0.value.toString())
                Log.d("Log_Review_Info2",reviewInfo.toString())
                initView()
            }
        })
    }

    /////////// Init View Pager ///////////
    private var y = -1f
    // Intercept Touch by activity with Y Position
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        y = ev!!.y
        Log.e("px", y.toString())
        // px2dp
        y /= (resources.displayMetrics.densityDpi / 160f)
        viewpager_review.isUserInputEnabled = (viewpager_review.currentItem == 0 || (viewpager_review.currentItem != 0 && y > 400) || (viewpager_review.currentItem == adapter.itemCount - 1 && y > 650))
        return super.dispatchTouchEvent(ev)
    }
}