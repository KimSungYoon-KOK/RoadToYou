package com.kok.roadtoyou.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kok.roadtoyou.R

class MyPageRecyclerViewAdapter(var items: ArrayList<MyItem>)
    : RecyclerView.Adapter<MyPageRecyclerViewAdapter.ViewHolder>() {

    var itemClickListener: OnItemClickListener ?= null

    interface OnItemClickListener {
        fun OnItemClick(data: MyItem, position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var planName: TextView = itemView.findViewById(R.id.tv_plan_name)
        var planPeriod: TextView = itemView.findViewById(R.id.tv_plan_period)

        init {
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_page, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.planName.text = items[position].name
        holder.planPeriod.text = items[position].period
    }

}