package com.kok.roadtoyou.ui.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kok.roadtoyou.MainActivity
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        init()
        initBtn()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initBtn() {
        //구글 로그인 버튼
        googleSignInBtn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        //회원 가입
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        //로그인 버튼
        signInBtn.setOnClickListener {
            val email = input_id.text.toString().trim()
            val pwd = input_pw.text.toString().trim()

            auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "회원 정보를 다시 확인해 주세요요", Toast.LENGTH_SHORT).show()
                   }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.result
            firebaseAuthWithGoogle(account)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {  //로그인 성공
                    val user = auth.currentUser
                    //Log.d("Log_User_Find_uid", user?.uid.toString())
                    //DB에 유저 정보 있는지 확인 하고 없으면 저장
                    val rdb = FirebaseDatabase.getInstance().getReference("users")
                    rdb.child("/${user?.uid}")
                        .addListenerForSingleValueEvent( object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
//                                TODO("Not yet implemented")
                            }
                            override fun onDataChange(p0: DataSnapshot) {
                                val userCheck = p0.value
                                //Log.d("Log_User_Find", userCheck.toString())
                                if (userCheck == null) {
                                    val userInfo = User(user?.uid, user?.displayName, null, null)
                                   // Log.d("Log_User_Info", userInfo.toString())
                                    rdb.child(user?.uid!!).setValue(userInfo)
                                }
                            }
                        })

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "회원 정보를 다시 확인해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}