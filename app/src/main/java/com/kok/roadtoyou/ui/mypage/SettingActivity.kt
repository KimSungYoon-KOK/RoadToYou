package com.kok.roadtoyou.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.kok.roadtoyou.App
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init()
    }

    private fun init() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            if (user.isEmailVerified) {
                Glide.with(this).load(photoUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .into(imageView_userPhoto)
                tv_userMail.text = email
                tv_userName.text = name
            } else {
                Glide.with(this).load(photoUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .into(imageView_userPhoto)
                tv_userMail.text = App.prefs.getUserMail()
                tv_userName.text = App.prefs.getUserName()
            }
        }
    }
}