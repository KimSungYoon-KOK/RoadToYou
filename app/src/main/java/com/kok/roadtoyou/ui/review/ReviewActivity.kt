package com.kok.roadtoyou.ui.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import com.kok.roadtoyou.DataConverter
import com.kok.roadtoyou.R

class ReviewActivity : AppCompatActivity() {

    lateinit var reviewDB: DatabaseReference
    lateinit var reviewInfo: ReviewInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        initData()
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