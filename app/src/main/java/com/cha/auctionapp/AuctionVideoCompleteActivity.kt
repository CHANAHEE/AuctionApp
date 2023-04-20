package com.cha.auctionapp

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.cha.auctionapp.databinding.ActivityAuctionVideoCompleteBinding


class AuctionVideoCompleteActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionVideoCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionVideoCompleteBinding.inflate(layoutInflater)
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
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, videoUri)

            val videoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
            val videoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0

            binding.videoview.layoutParams.width = videoWidth
            binding.videoview.layoutParams.height = videoHeight

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