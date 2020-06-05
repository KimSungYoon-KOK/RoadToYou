package com.kok.roadtoyou.ui.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        init()
    }

    private fun init() {
        supportActionBar?.title = "이메일로 회원가입"

        auth = FirebaseAuth.getInstance()

        joinBtn.setOnClickListener {
            val email = et_email.text.toString()
            val pwd = et_pwd.text.toString()
            val name = et_name.text.toString()

            if (email.length < 2 || pwd.length < 2 || name.length < 2) {
                Toast.makeText(this, "회원 정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!email.contains("@") || !email.contains(".com")) {
                Toast.makeText(this, "메일 형식을 지켜주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, NameActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "등록 오류: 잠시후 재시도 해주세요", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }
        }
    }
}