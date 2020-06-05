package com.kok.roadtoyou.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kok.roadtoyou.R

class SearchRecyclerViewAdapter(val flag: Boolean, val items: ArrayList<Places>):
        RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    var itemClickListener: OnItemClickListener? = null
    lateinit var context : Context

    interface OnItemClickListener {
        fun OnItemClick(view: View, position: Int)
        fun OnSelectClick(view: View, position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView
        var searchTitle: TextView
        var selectBtn: TextView

        init {
            thumbnail = itemView.findViewById(R.id.search_thumbnail)
            searchTitle = itemView.findViewById(R.id.search_title)
            selectBtn = itemView.findViewById(R.id.selectBtn)

            //아이템 클릭 -> 자세한 정보 Activity
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(it, adapterPosition)
            }

            //selectBtn 클릭 ->
            selectBtn.setOnClickListener {
                itemClickListener?.OnSelectClick(it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = items.get(position).url
        Glide.with(context).load(url)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_baseline_error_outline_24)
            .into(holder.thumbnail)

        holder.searchTitle.text = items.get(position).title
        if (flag) {
            holder.selectBtn.visibility = VISIBLE
        }

    }


}