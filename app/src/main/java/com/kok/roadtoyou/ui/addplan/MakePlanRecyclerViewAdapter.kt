package com.kok.roadtoyou.ui.addplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.kok.roadtoyou.R

class MakePlanRecyclerViewAdapter(option: FirebaseRecyclerOptions<AddPlaceItem>)
    : FirebaseRecyclerAdapter<AddPlaceItem, MakePlanRecyclerViewAdapter.ViewHolder>(option) {

    var itemClickListener: OnItemClickListener? = null


    interface OnItemClickListener {
        fun OnItemClick(view: View, position: Int)
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var placeCount: TextView = itemView.findViewById(R.id.tv_placeCount)
        var placeNm: TextView = itemView.findViewById(R.id.tv_placeNm)
        var placeType: TextView = itemView.findViewById(R.id.tv_placeType)
        var movebtn: ImageView = itemView.findViewById(R.id.movebtn)
        var syncbtn : ImageView = itemView.findViewById(R.id.syncbtn)
        var editbtn : ImageView = itemView.findViewById(R.id.editbtn)

        init {
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(it, adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_add_place, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: AddPlaceItem) {
        holder.placeCount.text = model.count.toString()
        holder.placeNm.text = model.placeInfo?.title
        holder.placeType.text = when (model.placeInfo?.type) {
            12 -> "관광지"
            14 -> "문화 시설"
            15 -> "행사/공연/축제"
            28 -> "레포츠"
            32 -> "숙박"
            39 -> "음식점"
            else -> "기타"
        }
    }

}