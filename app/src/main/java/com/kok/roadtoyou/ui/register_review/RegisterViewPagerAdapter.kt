package com.kok.roadtoyou.ui.register_review

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.kok.roadtoyou.R

class RegisterViewPagerAdapter(private val itemList: MutableList<ReviewItem>)
    :RecyclerView.Adapter<RegisterViewPagerAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var adapter: RegisterRecyclerViewAdapter
    var itemClickListener: OnItemClickListener ?= null

    interface OnItemClickListener {
        fun addPhotoOnClick(view: View, position: Int)
        fun addGalleryOnClick(view:View, position: Int)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val placeName:TextView = itemView.findViewById(R.id.tv_placeName)
        val review: EditText = itemView.findViewById(R.id.et_review)
        val addPhoto: ImageButton = itemView.findViewById(R.id.photoBtn)
        val addGallery: ImageButton = itemView.findViewById(R.id.galleryBtn)
        val hashTag: NachoTextView = itemView.findViewById(R.id.et_hashtag)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView_register_review)

        init {
            addPhoto.setOnClickListener {
                itemClickListener?.addPhotoOnClick(it,adapterPosition)
            }
            addGallery.setOnClickListener {
                itemClickListener?.addGalleryOnClick(it, adapterPosition)
            }

            hashTag.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
            hashTag.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context)
            .inflate(R.layout.viewpager_register_review, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeName.text = itemList[position].placeName

        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        holder.recyclerView.layoutManager = layoutManager
        if (itemList[position].imgList == null) itemList[position].imgList = ArrayList()
        adapter = RegisterRecyclerViewAdapter(itemList[position].imgList!!)
        adapter.itemClickListener = object: RegisterRecyclerViewAdapter.OnItemClickListener {
            override fun cancelOnClick(view: View, position2: Int) {
                itemList[position].imgList!!.removeAt(position2)
                adapter.notifyDataSetChanged()
            }
        }
        holder.recyclerView.adapter = adapter

        holder.review.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                itemList[position].review = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

        })

        holder.hashTag.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                itemList[position].hashTags = holder.hashTag.chipAndTokenValues
                //Log.d("Log_HashTag", itemList[position].hashTags.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("not implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("not implemented")
            }
        })

    }

}
