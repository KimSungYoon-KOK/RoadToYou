package com.kok.roadtoyou.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.kok.roadtoyou.R

class HomeAdapter(options: FirebaseRecyclerOptions<HomeItem>):
    FirebaseRecyclerAdapter<HomeItem, HomeAdapter.ViewHolder>(options) {

    var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun OnItemClick(view: View, position: Int)
        fun OnClipClick(view: View, position: Int)
        fun OnFavoriteClick(view: View, position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView
        var item_title: TextView
        var item_content: TextView
        var favorites: TextView         //좋아요 수
        var userID: TextView
        var favoriteBtn: ToggleButton
        var clipBtn: ImageView

        init {
            item_title = itemView.findViewById(R.id.item_title)
            thumbnail = itemView.findViewById(R.id.item_img)
            item_content = itemView.findViewById(R.id.item_content)
            favorites = itemView.findViewById(R.id.favorites)
            userID = itemView.findViewById(R.id.userID)
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn)
            clipBtn = itemView.findViewById(R.id.clipBtn)

            //리뷰 클릭 했을때 -> ReviewActivity 로 이동
            thumbnail.setOnClickListener {
                itemClickListener?.OnItemClick(it, adapterPosition)
            }

            //좋아요 버튼 클릭 -> 좋아요 on/off
            favoriteBtn.setOnClickListener {
                itemClickListener?.OnFavoriteClick(it, adapterPosition)
            }

            //스크랩 버튼 클릭 -> MakePlanActivity 로 이동
            clipBtn.setOnClickListener{
                itemClickListener?.OnClipClick(it, adapterPosition)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: HomeItem) {
        //TODO("Not yet implemented")
//        예시
//        holder.productId.text = model.pId.toString()
//        holder.productName.text = model.pName
//        holder.productQuantity.text = model.pQuantity.toString()
    }

}