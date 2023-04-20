package com.cha.auctionapp.activities

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityAuctionVideoCompleteBinding


class AuctionVideoCompleteActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionVideoCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.cha.auctionapp.databinding.ActivityAuctionVideoCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        var videoUri = Uri.parse(intent.getStringExtra("video"))
        binding.videoview.setVideoURI(videoUri)

        binding.videoview.setOnPreparedListener {
            binding.videoview.start()



        }
        binding.videoview.setOnCompletionListener { binding.videoview.start() }


    }




    inner class MotiveVideoView : VideoView {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }
}