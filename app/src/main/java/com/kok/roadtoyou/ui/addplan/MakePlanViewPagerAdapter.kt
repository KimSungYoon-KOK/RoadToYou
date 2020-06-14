package com.kok.roadtoyou.ui.addplan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.search.PlaceItem
import com.kok.roadtoyou.ui.search.SearchDetailActivity

class MakePlanViewPagerAdapter(private val itemList: ArrayList<ArrayList<AddPlaceItem>>)
    : RecyclerView.Adapter<MakePlanViewPagerAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var adapter: MakePlanRecyclerViewAdapter
    lateinit var mDatabase: DatabaseReference

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
        mDatabase = FirebaseDatabase.getInstance().getReference("")
        val query = FirebaseDatabase.getInstance().reference.child("").limitToLast(50)
        val option = FirebaseRecyclerOptions.Builder<AddPlaceItem>()
            .setQuery(query, AddPlaceItem::class.java)
            .build()
        adapter = MakePlanRecyclerViewAdapter(option)
        adapter.itemClickListener = object : MakePlanRecyclerViewAdapter.OnItemClickListener {
            override fun OnItemClick(view: View, position2: Int) {
                val intent = Intent(context, SearchDetailActivity::class.java)
                intent.putExtra("PLACE_DATA", itemList[position1][position2].placeInfo)
                (context as Activity).startActivityForResult(intent, 1500)
            }
        }
        holder.recyclerView.adapter = adapter
    }

}