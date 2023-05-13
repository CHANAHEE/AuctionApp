package com.cha.auctionapp.activities

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.exifinterface.media.ExifInterface
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityAuctionVideoCompleteBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.IOException


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

        setVideoView()
        binding.videoview.setOnPreparedListener { binding.videoview.start() }
        binding.btnComplete.setOnClickListener {
            startActivity(Intent(this,AuctionEditActivity::class.java)
            .putExtra("video",Uri.parse(intent.getStringExtra("video")).toString()))
        }
        binding.btnBack.setOnClickListener { finish() }
    }


    /*
    *
    *       VideoView 세팅
    *
    * */
    private fun setVideoView(){
        var videoUri = Uri.parse(intent.getStringExtra("video"))
        var videopath = getRealPathFromUri(videoUri)
        binding.videoview.setVideoPath(videopath)
    }
    fun getRealPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return cursor.getString(columnIndex)
        }
        return null
    }
}