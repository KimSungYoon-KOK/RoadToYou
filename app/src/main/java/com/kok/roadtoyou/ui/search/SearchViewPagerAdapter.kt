package com.kok.roadtoyou.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kok.roadtoyou.R

class SearchViewPagerAdapter (
    val flag: Boolean,
    val itemList: ArrayList<ArrayList<Places>>
) : RecyclerView.Adapter<SearchViewPagerAdapter.ViewHolder>() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: SearchRecyclerViewAdapter
    lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerView_search)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_search, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        holder.recyclerView.layoutManager = layoutManager
        adapter = SearchRecyclerViewAdapter(flag, itemList[position])
        holder.recyclerView.adapter = adapter
    }

}