package com.kok.roadtoyou.ui.addplan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.fragment_add_plan.*
import java.util.*

class AddPlanFragment : Fragment() {

    lateinit var planDB: DatabaseReference
    lateinit var userDB: DatabaseReference

    private lateinit var days: List<Calendar>

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
            val period = getPlanPeriod()

            if (period != null) {
                //Add Firebase
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    userDB = FirebaseDatabase.getInstance().getReference("users/${user.uid}")
                    val key = userDB.child("planList").push().key!!
                    userDB.child("planList/${key}").setValue(period)
                    planDB = FirebaseDatabase.getInstance().getReference("plans")
                    val item = PlanItem(
                        key,
                        period,     //planName: 임시로 period 저장
                        period,
                        days.size,
                        listOf(user.uid),
                        null
                    )
                    Log.d("Log_Plan_Info", item.toString())
                    planDB.child(key).setValue(item)

                    val intent = Intent(activity, MakePlanActivity::class.java)
                    intent.putExtra("ACTIVITY_FLAG",1)
                    intent.putExtra("PLAN_ITEM", item)
                    startActivity(intent)
                } else {
                    Log.e("NOT USER", "NOT CURRENT USER")
                }
            } else {
                Toast.makeText(activity, "날짜를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        //친구 초대하기 버튼
        addFriendBtn.setOnClickListener{
            //TODO: 친구 초대하기
        }
    }

    private fun getPlanPeriod(): String? {
        days = calendar_view.selectedDates      //List<Date>
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

}