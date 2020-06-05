package com.kok.roadtoyou.ui.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        init()
    }

    private fun init() {
        supportActionBar?.title = "이메일로 회원가입"

        mAuth = FirebaseAuth.getInstance()

        val fillOutTextWatcher = object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email = et_email.text.toString().trim()
                val pwd = et_pwd.text.toString().trim()
                joinBtn.isEnabled = email.isNotEmpty() && pwd.isNotEmpty()
            }
        }
        et_email.addTextChangedListener(fillOutTextWatcher)
        et_pwd.addTextChangedListener(fillOutTextWatcher)

        joinBtn.setOnClickListener {
            val email = et_email.text.toString().trim()
            val pwd = et_pwd.text.toString().trim()

            if (!email.contains("@") || !email.contains(".com")) {
                Toast.makeText(this, "메일 형식을 지켜주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pwd.length < 6) {
                Toast.makeText(this, "비밀 번호는 6글자 이상이여야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, NameActivity::class.java)
                        intent.putExtra("EMAIL",email)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w("createUserWithEmail:failure", it.getException())
                        Toast.makeText(this, "등록 오류: 잠시후 재시도 해주세요", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }
        }
    }
}