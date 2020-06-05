package com.kok.roadtoyou.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kok.roadtoyou.R

class SearchRecyclerViewAdapter(val flag: Boolean, val items: ArrayList<Places>):
        RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

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
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = items.get(position).url
//        Glide.with(context).load(url).placeholder(R.drawable.logo).into(holder.thumbnail)

//        if(data != null){
//            if(data.url != null){
//            } else {
//                Glide.with(context).load(R.drawable.logo).into(holder.thumbnail)
//                Log.v("Thumbnail2", "null")
//            }
//        }

        holder.searchTitle.text = items.get(position).title
        if (flag) {
            holder.selectBtn.visibility = VISIBLE
        }

    }


}