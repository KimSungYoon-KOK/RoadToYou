package com.kok.roadtoyou.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import com.google.android.material.tabs.TabLayoutMediator
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_search.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLEncoder

class SearchActivity : AppCompatActivity() {

    private val key = "V3TPLc8KikVyK235xNyOorabnl1eDnekQJSTWtpl4eQXyE3MWxAUjlZXJo6PIxrmLZGlixdOVWTSs8PmCfb4nQ%3D%3D"
    private val suffix = "MobileOS=AND&MobileApp=roadtoyou"

    private val itemList = ArrayList<ArrayList<PlaceItem>>()
    lateinit var adapter: SearchViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //handleIntent(intent)
        init()
        initViewPager()
    }

    ////////////////////////////////////// ViewPager2 init /////////////////////////////////////////
    private fun init() {
        for (i in 0..3)
            itemList.add(ArrayList())
    }

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
        val url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword?ServiceKey=$key&keyword=$keyword&$suffix"
        Log.d("Log_URL", url)
        task.execute(URL(url))
    }

    class MyAsyncTask(context: SearchActivity): AsyncTask<URL, Unit, Unit>() {

        val activityreference = WeakReference(context)

        override fun doInBackground(vararg params: URL?): Unit {
            val activity = activityreference.get()
            activity?.adapter?.itemList?.clear()
           for (i in 0..3)
                activity?.adapter?.itemList?.add(ArrayList())


            val doc = Jsoup.connect(params[0].toString()).parser(Parser.xmlParser()).get()
            val items = doc.select("item")
            //val totalCount = doc.select("totalCount").text().toString().toInt()
            for (item in items) {

//                val temp1 = item.select("contentid").text().toString()             //Place ID
//                val temp2 = item.select("title").text().toString()                        //Place Title
//                val temp3 = item.select("contenttypeid").text().toString()         //Place Type
//                val temp4 = item.select("mapx").text().toString()               //lng(경도)
//                val temp5 = item.select("mapy").text().toString()               //lat(위도)
//                val temp6 = item.select("addr1").text().toString()                         //Addr 1
//                val temp7 = item.select("addr2").text().toString()                         //Addr 2
//                val temp8 = item.select("tel").text().toString()                           //Tel
//                val temp9 = item.select("firstimage2").text().toString()                    //URL
//
//                if (temp1.isEmpty() || temp2.isEmpty() || temp3.isEmpty() || temp4.isEmpty() || temp5.isEmpty() || temp6.isEmpty() || temp7.isEmpty() || temp8.isEmpty() || temp9.isEmpty() ) {
//                    Log.d("Log_TEMP", temp1+" / "+temp2+" / "+temp3+" / "+temp4+" / "+temp5+" / "+temp6+" / "+temp7+" / "+temp8+" / "+temp9)
//                }

                val tempItem = PlaceItem(
                    item.select("contentid").text().toString().toInt(),             //Place ID
                    item.select("title").text().toString(),                         //Place Title
                    item.select("contenttypeid").text().toString().toInt(),         //Place Type
                    item.select("mapx").text().toString().toDouble(),               //lng(경도)
                    item.select("mapy").text().toString().toDouble(),               //lat(위도)
                    item.select("addr1").text().toString(),                         //Addr 1
                    item.select("addr2").text().toString(),                         //Addr 2
                    item.select("tel").text().toString(),                           //Tel
                    item.select("firstimage2").text().toString())                    //URL
                Log.d("ITEM", tempItem.toString())

                // ContentType : 관광지 12   /   문화시설 14 행사/공연/축제 15 레포츠 28    /   음식점 39   /   숙박 32
                val contentType = tempItem.type
                when(contentType) {
                    12 -> activity?.adapter!!.itemList[0].add(tempItem)
                    39 -> activity?.adapter!!.itemList[2].add(tempItem)
                    32 -> activity?.adapter!!.itemList[3].add(tempItem)
                    else -> activity?.adapter!!.itemList[1].add(tempItem)
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            val activity = activityreference.get()
            if (activity == null || activity.isFinishing)
                return
            activity.adapter.notifyDataSetChanged()
        }
    }



    ////////////////////////////////////// Search Bar 구현 /////////////////////////////////////////
//    private fun handleIntent(intent: Intent) {
//        if (Intent.ACTION_SEARCH == intent.action) {
//            val query = intent.getStringExtra(SearchManager.QUERY)
//            //use the query to search your data somehow
//        }
//    }
//
//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        handleIntent(intent)
//    }

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