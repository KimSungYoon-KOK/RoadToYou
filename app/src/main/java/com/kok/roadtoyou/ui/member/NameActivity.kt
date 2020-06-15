package com.kok.roadtoyou.ui.member

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kok.roadtoyou.App
import com.kok.roadtoyou.MainActivity
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_name.*

class NameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)
        init()
    }

    private fun init() {
        //이름 3글자 이하이면 버튼 false
        et_name.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, count: Int, p3: Int) {
                val input = et_name.text.toString().trim().length
                startBtn.isEnabled = (input >= 2)
            }
        })

        startBtn.setOnClickListener {
            val name = et_name.text.toString().trim()
            if(initUserInfo(name)) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "계정 등록 오류", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    private fun initUserInfo(name: String):Boolean {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null){
            //DB에 현재 유저 정보 업로드
            val rdb = FirebaseDatabase.getInstance().getReference("users")
            val userInfo = User(user.uid, name, null, null)
            rdb.child(user.uid).setValue(userInfo)
            return true
        }
        return false
    }
}