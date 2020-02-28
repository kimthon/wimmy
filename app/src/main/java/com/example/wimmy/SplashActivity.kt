package com.example.wimmy

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.db.PhotoViewModel

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PhotoScanner.addAllImages(this, ViewModelProviders.of(this).get(PhotoViewModel::class.java))
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

