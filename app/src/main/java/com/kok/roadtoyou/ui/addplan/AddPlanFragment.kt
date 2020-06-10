package com.kok.roadtoyou.ui.addplan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_make_plan.*
import kotlinx.android.synthetic.main.fragment_add_plan.*
import java.util.*

class AddPlanFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_plan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initCalendar()
        initBtn()
    }

    private fun initCalendar(){
        calendar_view.weekendDayTextColor = Color.parseColor("#FF0000")
        calendar_view.selectionType = SelectionType.RANGE
        calendar_view.currentDayIconRes = R.drawable.ic_baseline_arrow_drop_down_24
        calendar_view.selectedDayBackgroundStartColor = Color.parseColor("#0d357a")
        calendar_view.selectedDayBackgroundEndColor = Color.parseColor("#0d357a")
        calendar_view.selectedDayBackgroundColor = Color.parseColor("#245598")
    }

    private fun initBtn() {

        //일정 만들기 버튼
        continueBtn.setOnClickListener {
            val days = calendar_view.selectedDates      //List<Date>
            if(days.size != 0){
                val start = days[0]
                val startDate = start.get(Calendar.DAY_OF_MONTH).toString()
                val startMonth = (start.get(Calendar.MONTH) + 1).toString()
                val startYear = start.get(Calendar.YEAR).toString()

                val end = days.get(days.size - 1)
                val endDate = end.get(Calendar.DAY_OF_MONTH).toString()
                val endMonth = (end.get(Calendar.MONTH) + 1).toString()
                val endYear = end.get(Calendar.YEAR).toString()

                var resultStr = ""
                if(startYear == endYear)
                    resultStr = "$startYear.$startMonth.$startDate - $endMonth.$endDate"

                if(startYear == endYear && startMonth == endMonth && startDate == endDate)
                    resultStr = "$startYear.$startMonth.$startDate"

                //Add Firebase
                val rdb = FirebaseDatabase.getInstance().getReference("Products/items")
                val planId = 1
                val item = PlanItem(
                    resultStr,
                    planId,
                    null
                )
                rdb.child(planId.toString()).setValue(item)

                val intent = Intent(activity, MakePlanActivity::class.java)
                intent.putExtra("START_DATE", "$startYear-$startMonth-$startDate")
                intent.putExtra("END_DATE", "$endYear-$endMonth-$endDate")
                intent.putExtra("PLAN_DATE", resultStr)
                intent.putExtra("PLAN_RANGE", days.size)
                intent.putExtra("FLAG_KEY",1)
                startActivity(intent)

            } else{
                Toast.makeText(activity, "날짜를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        //친구 초대하기 버튼
        addFriendBtn.setOnClickListener{
            //TODO: 친구 초대하기
        }
    }
}