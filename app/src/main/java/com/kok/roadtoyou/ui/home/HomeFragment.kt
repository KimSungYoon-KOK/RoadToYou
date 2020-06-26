package com.kok.roadtoyou.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.addplan.MakePlanActivity
import com.kok.roadtoyou.ui.register_review.ReviewInfo
import com.kok.roadtoyou.ui.review.ReviewActivity
import com.kok.roadtoyou.ui.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: HomeAdapter

    lateinit var rdb: DatabaseReference
    var findQuery = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView_home.layoutManager = layoutManager
        rdb = FirebaseDatabase.getInstance().getReference("reviews")
        val query = rdb.limitToLast(50)
        val option = FirebaseRecyclerOptions.Builder<ReviewInfo>()
            .setQuery(query, ReviewInfo::class.java)
            .build()
        adapter = HomeAdapter(option)
        adapter.itemClickListener = object :HomeAdapter.OnItemClickListener {
            override fun OnItemClick(view: View, position: Int) {
                val intent = Intent(activity, ReviewActivity::class.java)
                intent.putExtra("REVIEW_ID", adapter.getItem(position).reviewId)
                startActivity(intent)
            }
            override fun OnClipClick(view: View, position: Int) {
                clipping(position)
            }
            override fun OnFavoriteClick(view: View, position: Int) {
                favoriteClick(position)
            }
        }
        recyclerView_home.adapter = adapter
    }

    private fun clipping(position: Int) {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("일정을 스크랩 합니다.")
            .setMessage("이 일정을 스크랩 하여 새로운 일정을 생성 합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                val intent = Intent(activity, MakePlanActivity::class.java)
                intent.putExtra("ACTIVITY_FLAG", 2)
                intent.putExtra("PLAN_ID", adapter.getItem(position).planId)
                dialog.dismiss()
                startActivity(intent)
            }
            .setNegativeButton("취소") { _, _ ->  }
            .create()
            .show()
    }

    private fun favoriteClick(position: Int){
        val reviewId = adapter.getItem(position).reviewId
        rdb.child("$reviewId/favorites").setValue(adapter.getItem(position).favorites + 1)
    }


    ///////////////////////////////// Adapter Listening Start & Stop ///////////////////////////////
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    ////////////////////////////////////// AppBar Menu Setting /////////////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_search -> {
                val intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent)
            }
            else -> return false
        }
        return true
    }




}