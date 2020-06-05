package com.kok.roadtoyou.ui.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.kok.roadtoyou.App
import com.kok.roadtoyou.MainActivity
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_name.*
import kotlinx.android.synthetic.main.activity_setting.*

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
                startBtn.isEnabled = (input >= 3)
            }
        })

        startBtn.setOnClickListener {
            if(initUserInfo(et_name.text.toString().trim())) {
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
        val email: String
        val uid: String
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null){
            email = user.email.toString()
            uid = user.uid
            Log.d("USERINFO", email + "/"+name+"/" + uid)
            App.prefs.setUserInfo(email, name, uid)
            return true
        }
        return false
    }
}