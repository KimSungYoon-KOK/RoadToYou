package com.kok.roadtoyou.ui.mypage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kok.roadtoyou.MainActivity
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.addplan.MakePlanActivity
import com.kok.roadtoyou.ui.register_review.RegisterReviewActivity
import com.kok.roadtoyou.ui.review.ReviewActivity

class MyPageViewPagerAdapter(private val items: ArrayList<ArrayList<MyItem>>)
    : RecyclerView.Adapter<MyPageViewPagerAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var adapter: MyPageRecyclerViewAdapter

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView_my_page)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context)
            .inflate(R.layout.viewpager_my_page, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        holder.recyclerView.layoutManager = layoutManager
        adapter = MyPageRecyclerViewAdapter(items[position])
        adapter.itemClickListener = object : MyPageRecyclerViewAdapter.OnItemClickListener {
            override fun OnItemClick(data: MyItem, position2: Int) {
                when(position) {
                    0 -> { goMakePlanActivity(data) }
                    1 -> { goRegisterReview(data) }
                    2 -> { goReview(data) }
                }
            }
        }
        holder.recyclerView.adapter = adapter

    }

    //일정 작성 페이지
    fun goMakePlanActivity(data: MyItem) {
        val intent = Intent(context, MakePlanActivity::class.java)
        intent.putExtra("PLAN_ID", data.id)
        (context as MainActivity).startActivityForResult(intent, 1000)
    }

    //리뷰 등록 페이지
    fun goRegisterReview(data: MyItem) {
        val intent = Intent(context, RegisterReviewActivity::class.java)
        intent.putExtra("PLAN_ID", data.id)
        (context as MainActivity).startActivityForResult(intent, 2000)
    }

    //리뷰 보기 페이지
    fun goReview(data: MyItem) {
        val intent = Intent(context, ReviewActivity::class.java)
        intent.putExtra("REVIEW_ID", data.id)
        (context as MainActivity).startActivityForResult(intent, 3000)
    }


}