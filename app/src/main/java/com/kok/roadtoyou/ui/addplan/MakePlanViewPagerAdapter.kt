package com.kok.roadtoyou.ui.addplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.kok.roadtoyou.R
import java.util.*
import kotlin.collections.ArrayList

class MakePlanViewPagerAdapter(
    val addListener: ViewPagerAdapterEventListener,
    private val itemList: ArrayList<ArrayList<AddPlaceItem>>
) : RecyclerView.Adapter<MakePlanViewPagerAdapter.ViewHolder>(), MakePlanRecyclerViewAdapter.OnStartDragListener {

    lateinit var context: Context

    lateinit var mCallback: ItemTouchHelperCallback
    lateinit var mItemTouchHelper : ItemTouchHelper

    interface ViewPagerAdapterEventListener {
        fun onChangeCallback2(view: View, itemList: ArrayList<ArrayList<AddPlaceItem>>)
    }

    override fun onStartDrag(dragHolder: MakePlanRecyclerViewAdapter.ViewHolder) {
        mItemTouchHelper.startDrag(dragHolder)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView_make_plan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context)
            .inflate(R.layout.viewpager_make_plan, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position1: Int) {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        holder.recyclerView.layoutManager = layoutManager
        val listener = object: MakePlanRecyclerViewAdapter.RecyclerViewAdapterEventListener{
            override fun onChangeCallback(view: View, items: ArrayList<AddPlaceItem>) {
                addListener.onChangeCallback2(view, itemList)
            }
        }
        val adapter = MakePlanRecyclerViewAdapter(itemList[position1], listener, this)
        mCallback = ItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(mCallback)
        holder.recyclerView.adapter = adapter
    }

}