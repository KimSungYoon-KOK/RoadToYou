package com.kok.roadtoyou.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.addplan.MakePlanActivity

class SearchViewPagerAdapter (
    private val flag: Boolean,
    val itemList: ArrayList<ArrayList<PlaceItem>>
) : RecyclerView.Adapter<SearchViewPagerAdapter.ViewHolder>() {

    private lateinit var adapter: SearchRecyclerViewAdapter
    lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView_search)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_search, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position1: Int) {
        holder.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = SearchRecyclerViewAdapter(flag, itemList[position1])
        adapter.itemClickListener = object : SearchRecyclerViewAdapter.OnItemClickListener {
            override fun OnItemClick(view: View, position2: Int) {
                val intent = Intent(context, SearchDetailActivity::class.java)
                intent.putExtra("FLAG", flag)       // 시작 액티비티가 SearchActivity == false, AddPlaceActivity == true
                intent.putExtra("PLACE_DATA", itemList[position1][position2])
                context.startActivity(intent)
            }

            override fun OnSelectClick(view: View, position2: Int) {
                val intent = Intent(context, MakePlanActivity::class.java)
                intent.putExtra("PLACE_DATA", itemList[position1][position2])
                //Log.d("Log_PLACE_DATA", itemList[position1][position2].toString())
                (context as Activity).setResult(Activity.RESULT_OK, intent)
                (context as Activity).finish()
            }
        }
        holder.recyclerView.adapter = adapter
    }

}