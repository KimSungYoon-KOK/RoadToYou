package com.kok.roadtoyou.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLEncoder

class SearchActivity : AppCompatActivity() {

    val key = "V3TPLc8KikVyK235xNyOorabnl1eDnekQJSTWtpl4eQXyE3MWxAUjlZXJo6PIxrmLZGlixdOVWTSs8PmCfb4nQ%3D%3D"
    val suffix = "MobileOS=ETC&MobileApp=AppTest"

    val itemList = ArrayList<ArrayList<Places>>(4)
    lateinit var adapter: SearchViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        handleIntent(intent)
        initViewPager()

    }

    ////////////////////////////////////// ViewPager2 init /////////////////////////////////////////
    private fun initViewPager() {
        //TODO: intent로 flag 선언
        val flag = intent.hasExtra("FLAG")
        adapter = SearchViewPagerAdapter(flag, itemList)
        viewPager_search.adapter = adapter

        //TabLayout Init
        // ContentType : 관광지 12   /   문화시설 14 행사/공연/축제 15 레포츠 28    /   음식점 39   /   숙박 32
        val tabLayoutTextArray = arrayOf("관광", "문화/쇼핑","맛집", "숙박")
        TabLayoutMediator(tabLayout_search, viewPager_search) { tab, position ->
            tab.text = tabLayoutTextArray[position]
        }.attach()
    }



    //////////////////////////////////////// API Parsing ///////////////////////////////////////////
    fun startTask(query: String?) {
        val task = MyAsyncTask(this)
        val keyword = URLEncoder.encode(query, "UTF-8")
        task.execute(URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword?ServiceKey=$key&keyword=$keyword&$suffix"))
    }

    class MyAsyncTask(context: SearchActivity): AsyncTask<URL, Unit, Unit>() {

        val activityreference = WeakReference(context)

        override fun doInBackground(vararg p0: URL?): Unit {
            val activity = activityreference.get()
            activity?.adapter?.itemList?.clear()

            val doc = Jsoup.connect(p0[0].toString()).get()
            val totalCount = doc.select("totalCount").toString().toInt()
            for (i in 0 until totalCount) {
                val item = Places(
                    doc.select("contentid").toString().toInt(),             //Place ID
                    doc.select("title").toString(),                         //Place Title
                    doc.select("contenttypeid").toString().toInt(),         //Place Type
                    doc.select("mapx").toString().toDouble(),               //Location X
                    doc.select("mapy").toString().toDouble(),               //Location Y
                    doc.select("addr1").toString(),                         //Addr 1
                    doc.select("addr2").toString(),                         //Addr 2
                    doc.select("tel").toString(),                           //Tel
                    //doc.select(""),                                         //Servertype
                    1,
                    doc.select("").toString()                               //URL
                )

                // ContentType : 관광지 12   /   문화시설 14 행사/공연/축제 15 레포츠 28    /   음식점 39   /   숙박 32
                val contentType = item.type
                when(contentType) {
                    12 -> activity?.adapter!!.itemList[0].add(item)
                    39 -> activity?.adapter!!.itemList[2].add(item)
                    32 -> activity?.adapter!!.itemList[3].add(item)
                    else -> activity?.adapter!!.itemList[1].add(item)
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityreference.get()
            if (activity == null || activity.isFinishing)
                return
        }
    }



    ////////////////////////////////////// Search Bar 구현 /////////////////////////////////////////
    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search your data somehow
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem =  menu?.findItem(R.id.searchBtn)
        val searchView =searchItem?.actionView as SearchView
        searchView.apply {
            setSearchableInfo(manager.getSearchableInfo(componentName))
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.requestFocus()
                searchView.setQuery("",false)
                searchItem.collapseActionView()
                Toast.makeText(this@SearchActivity, "Looking for $query", Toast.LENGTH_SHORT).show()
                startTask(query)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
        return true
    }
}