package com.kok.roadtoyou.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.search.SearchActivity

class MyPageFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }





    ////////////////////////////////////// AppBar Menu Setting /////////////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_page_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_setting -> {
                val intent = Intent(activity, SettingActivity::class.java)
                startActivity(intent)
            }
            else -> return false
        }
        return true
    }
}