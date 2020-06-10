package com.kok.roadtoyou.ui.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kok.roadtoyou.R

class SearchDetailViewPagerAdapter(val items: ArrayList<String>)
    : RecyclerView.Adapter<SearchDetailViewPagerAdapter.ViewHolder>(){

    lateinit var context: Context

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.search_detail_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.viewpager_search_detail, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = items[position]
        val url = data.replace("\\","")
        //Log.d("IMG_search_detail", url)
        Glide.with(context).load(url).thumbnail(0.1f).placeholder(R.drawable.ic_baseline_error_outline_24).into(holder.img)
    }


}
