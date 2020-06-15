package com.kok.roadtoyou.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.kok.roadtoyou.App
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.member.User
import kotlinx.android.synthetic.main.fragment_my_page.*

class MyPageFragment : Fragment() {

    private lateinit var mDatebase: DatabaseReference
    lateinit var userInfo: User

    var gson = Gson()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUserInfo()
    }

    private fun initUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user !== null) {
            mDatebase = FirebaseDatabase.getInstance().getReference("users/${user.uid}")

            //User 정보
            mDatebase.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
//                    TODO("Not yet implemented")
                }
                override fun onDataChange(p0: DataSnapshot) {
                    Log.d("asdfaeqweq",p0.value.toString())
                    userInfo = gson.fromJson(p0.value.toString(), User::class.java)
                    Log.d("Log_User_Info", userInfo.toString())

                    //user 프로필 사진
                    Glide.with(activity!!).load(user.photoUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .into(imageView_userPhoto)
                    //User name
                    tv_userName.text = userInfo.name
                }
            })
        }
    }

}