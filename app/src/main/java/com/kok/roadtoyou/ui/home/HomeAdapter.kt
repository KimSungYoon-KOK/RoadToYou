package com.kok.roadtoyou.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.register_review.ReviewInfo

class HomeAdapter(options: FirebaseRecyclerOptions<ReviewInfo>):
    FirebaseRecyclerAdapter<ReviewInfo, HomeAdapter.ViewHolder>(options) {

    lateinit var context: Context
    var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun OnItemClick(view: View, position: Int)
        fun OnClipClick(view: View, position: Int)
        fun OnFavoriteClick(view: View, position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var thumbnail: ImageView = itemView.findViewById(R.id.item_img)
        var title: TextView = itemView.findViewById(R.id.item_title)
        var period: TextView = itemView.findViewById(R.id.item_period)
        var favorites: TextView = itemView.findViewById(R.id.favorites)         //좋아요 수
        var userID: TextView = itemView.findViewById(R.id.userID)
        var favoriteBtn: ToggleButton = itemView.findViewById(R.id.favoriteBtn)
        var clipBtn: ImageView = itemView.findViewById(R.id.clipBtn)

        init {

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
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ReviewInfo) {
        holder.title.text = model.reviewName
        holder.period.text = model.period
        holder.userID.text = "ⓒ${model.userId}"
        holder.favorites.text = model.favorites.toString()
        Glide.with(context).load(model.coverImg).thumbnail(0.1f).placeholder(R.drawable.ic_baseline_error_outline_24).into(holder.thumbnail)
    }

}