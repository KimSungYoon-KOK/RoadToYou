package com.kok.roadtoyou.ui.addplan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kok.roadtoyou.R
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
        initBtn()
    }

    private fun initBtn() {

        //일정 만들기 버튼
        continueBtn.setOnClickListener {
            //TODO: 달력에서 선택한 시작 날짜와 끝나는 날짜 변수에 저장
            //var startDate
            //var endDate
            var r = 1
            if(r != 0){
                val intent = Intent(activity, MakePlanActivity::class.java)
                //날짜 변수 intent 전달
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