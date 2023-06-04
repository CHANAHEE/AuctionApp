package com.cha.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.cha.auctionapp.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Glide.with(this).load(R.drawable.loadingicon).into(findViewById(R.id.iv_loading))

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,SNSLoginActivity::class.java))
            finish()
        },1500)
    }
}