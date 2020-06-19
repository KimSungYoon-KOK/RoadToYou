package com.kok.roadtoyou.ui.review

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kok.roadtoyou.R

class RegisterRecyclerViewAdapter(private val imgList: ArrayList<String>)
    : RecyclerView.Adapter<RegisterRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context
    var itemClickListener: OnItemClickListener ?= null

    interface OnItemClickListener {
        fun cancelOnClick(view: View, position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img)
        private val cancelBtn: ImageView = itemView.findViewById(R.id.cancelBtn)

        init {
            cancelBtn.setOnClickListener {
                itemClickListener?.cancelOnClick(it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_register_review, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return imgList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(imgList[position]).thumbnail(0.1f)
            .placeholder(R.drawable.ic_baseline_error_outline_24)
            .into(holder.imageView)
    }

}