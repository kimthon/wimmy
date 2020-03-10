package com.example.wimmy

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo == null) {
            val dlg: AlertDialog.Builder = AlertDialog.Builder(
                this@SplashActivity,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
            )
            dlg.setTitle("환영합니다") //제목

            dlg.setMessage("자동 태그 분석을 위해 와이파이나 데이터를 연결해주세요.") // 메시지
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                startActivity(intent)
                finish()
            })
            dlg.show()
        }
        else startActivity(intent)
    }
}

