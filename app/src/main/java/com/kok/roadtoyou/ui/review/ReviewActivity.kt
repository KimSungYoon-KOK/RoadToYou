package com.kok.roadtoyou.ui.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.database.*
import com.kok.roadtoyou.DataConverter
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.register_review.ReviewInfo

class ReviewActivity : AppCompatActivity() {

    lateinit var reviewDB: DatabaseReference
    lateinit var reviewInfo: ReviewInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        initView()
        initData()
    }

    private fun initView() {
        val toast = Toast.makeText(this, "좌우로 드래그 하세요", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 50)
        toast.show()
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
            }
        })
    }
}