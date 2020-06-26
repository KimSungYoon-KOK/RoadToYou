package com.kok.roadtoyou.ui.addplan

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_edit_date.*
import kotlinx.android.synthetic.main.fragment_add_plan.*
import java.util.*

class EditDateActivity : AppCompatActivity() {

    private lateinit var days: List<Calendar>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_date)

        initToolbar()           //툴바 세팅
        initBtn()               //새로운 일정 버튼 클릭 리스너
        calendar()
    }

    fun initToolbar(){
        //toolbar 커스텀 코드
        val mToolbar = findViewById<Toolbar>(R.id.toolbar_edit_plan_date)
        setSupportActionBar(mToolbar)
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)

        actionBar.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24) //뒤로가기 버튼을 본인이 만든 아이콘으로 하기 위해 필요
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun calendar(){
        // Setting
        calendar_view_edit.weekendDayTextColor = Color.parseColor("#FF0000")

        calendar_view_edit.selectionType = SelectionType.RANGE

        calendar_view_edit.currentDayIconRes = R.drawable.ic_baseline_arrow_drop_down_24

        calendar_view_edit.selectedDayBackgroundStartColor = Color.parseColor("#0d357a")
        calendar_view_edit.selectedDayBackgroundEndColor = Color.parseColor("#0d357a")
        calendar_view_edit.selectedDayBackgroundColor = Color.parseColor("#245598")

    }

    private fun initBtn() {

        editBtn.setOnClickListener {
            // Range Picker
            val period = getPlanPeriod()

            if(period != null){
                val resultIntent = Intent()
                resultIntent.putExtra("EDIT_PERIOD",period)
                resultIntent.putExtra("EDIT_DAYS",days.size)
                setResult(Activity.RESULT_OK,resultIntent)
                finish()
            } else{
                Toast.makeText(this,"수정을 취소하려면 뒤로가기 버튼을 클릭하세요", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun getPlanPeriod(): String? {
        days = calendar_view_edit.selectedDates      //List<Date>

        if(days.isNotEmpty()) {
            val start = days[0]
            val startDate = start.get(Calendar.DAY_OF_MONTH).toString()
            val startMonth = (start.get(Calendar.MONTH) + 1).toString()
            val startYear = start.get(Calendar.YEAR).toString()

            val end = days[days.size - 1]
            val endDate = end.get(Calendar.DAY_OF_MONTH).toString()
            val endMonth = (end.get(Calendar.MONTH) + 1).toString()
            val endYear = end.get(Calendar.YEAR).toString()

            var resultStr = "$startYear.$startMonth.$startDate - $startYear.$endMonth.$endDate"
            if (startYear == endYear)
                resultStr = "$startYear.$startMonth.$startDate - $endMonth.$endDate"

            if (startYear == endYear && startMonth == endMonth && startDate == endDate)
                resultStr = "$startYear.$startMonth.$startDate"

            return resultStr
        }else{
            return null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}