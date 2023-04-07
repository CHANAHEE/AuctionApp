package com.cha.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.os.postDelayed
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var ivLoading : ImageView = findViewById(R.id.iv_loading)
        Glide.with(this).load(R.drawable.loadingicon).into(ivLoading)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        },1500)
    }
}