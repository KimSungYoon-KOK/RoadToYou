package com.kok.roadtoyou

import android.app.Activity
import android.app.AlertDialog
import android.widget.Toast

class BackPressCloseHandler(private val activity: Activity) {
    private var backKeyPressedTime: Long = 0
    private var toast: Toast? = null
    private val flag = 0

    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish()
            toast!!.cancel()
        }
    }

    private fun showGuide() {
        Toast.makeText(activity, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
    }

    fun onBackPressed_save() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("종료 하시겠습니까?")
            .setMessage("이 페이지를 나가면 저장 되지 않습니다.")
            .setPositiveButton("나가기") { _, _ -> activity.finish() }
            .setNegativeButton("취소") { _, _ -> }
        val alertDialog = builder.create()
        alertDialog.show()
    }

}