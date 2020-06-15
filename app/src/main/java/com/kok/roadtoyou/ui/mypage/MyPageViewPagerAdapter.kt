package com.kok.roadtoyou.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.addplan.PlanItem

class MyPageViewPagerAdapter(val items: ArrayList<ArrayList<PlanItem>>)
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
            override fun OnItemClick(data: PlanItem, position2: Int) {
                when(position) {
                    0 -> { goMakePlanActivity() }
                    1 -> { goRegisterReview() }
                    2 -> { goReview() }
                }
            }
        }
        holder.recyclerView.adapter = adapter

    }

    //일정 작성 페이지
    fun goMakePlanActivity() {

    }

    //리뷰 등록 페이지
    fun goRegisterReview() {

    }

    //리뷰 보기 페이지
    fun goReview() {

    }

}