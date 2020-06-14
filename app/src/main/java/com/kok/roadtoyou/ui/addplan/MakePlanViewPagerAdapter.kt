package com.kok.roadtoyou.ui.addplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.kok.roadtoyou.R

class MakePlanViewPagerAdapter(private val itemList: ArrayList<ArrayList<AddPlaceItem>>)
    : RecyclerView.Adapter<MakePlanViewPagerAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var adapter: MakePlanRecyclerViewAdapter


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView_make_plan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context)
            .inflate(R.layout.viewpager_make_plan, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position1: Int) {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        holder.recyclerView.layoutManager = layoutManager
        adapter = MakePlanRecyclerViewAdapter(itemList[position1])
        holder.recyclerView.adapter = adapter
    }

}