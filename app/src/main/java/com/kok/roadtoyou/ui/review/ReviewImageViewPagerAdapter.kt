package com.kok.roadtoyou.ui.review

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kok.roadtoyou.R

class ReviewImageViewPagerAdapter (private val itemList:List<String>)
    : RecyclerView.Adapter<ReviewImageViewPagerAdapter.ViewHolder>(){

    lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image: ImageView = itemView.findViewById(R.id.review_image_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.viewpager_review_image, parent, false)
        return ViewHolder(v)
    }
    override fun getItemCount():Int{
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(itemList[position]).thumbnail(0.1f).placeholder(R.drawable.ic_baseline_error_outline_24).into(holder.image)
    }


}