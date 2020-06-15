package com.kok.roadtoyou.ui.addplan

import android.content.ContentValues.TAG
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
import kotlin.collections.HashMap

class AddPlanFragment : Fragment() {

    lateinit var mDB: DatabaseReference
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
                    mDB = FirebaseDatabase.getInstance().reference
                    val key = mDB.child("plans").push().key
                    if (key == null) {
                        Log.w(TAG, "Couldn't get push key for planList")
                        return@setOnClickListener
                    }

                    val plan = PlanItem(key, period, period, days.size, listOf(user.uid), null)
                    val planValues = plan.toMap()

                    val childUpdates = HashMap<String, Any>()
                    childUpdates["/plans/$key"] = planValues
                    childUpdates["/users/${user.uid}/planList"] = planValues

                    mDB.updateChildren(childUpdates)

                    val intent = Intent(activity, MakePlanActivity::class.java)
                    intent.putExtra("ACTIVITY_FLAG",1)
                    intent.putExtra("PLAN_ITEM", plan)
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