package com.cha.auctionapp.activities

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.exifinterface.media.ExifInterface
import androidx.loader.content.CursorLoader
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityAuctionVideoCompleteBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.IOException


class AuctionVideoCompleteActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionVideoCompleteBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionVideoCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun init() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setVideoView()
        val videoUri : Uri? = intent.getParcelableExtra("video",Uri::class.java)
        binding.videoview.setOnPreparedListener { binding.videoview.start() }
        binding.btnComplete.setOnClickListener {
            startActivity(Intent(this,AuctionEditActivity::class.java)
            .putExtra("video",videoUri))
        }
        binding.btnBack.setOnClickListener { finish() }
    }


    /*
    *
    *       VideoView 세팅
    *
    * */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setVideoView(){
        var videoUri = Uri.parse(intent.getParcelableExtra("video",Uri::class.java).toString())
        var videopath = getFilePathFromUri(videoUri)
        binding.videoview.setVideoPath(videopath)
    }
    /*
    *
    *       Uri -> File 변환
    *
    * */
    fun getFilePathFromUri(uri: Uri?): String? {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        val loader = CursorLoader(
            this,
            uri!!, proj, null, null, null
        )
        val cursor = loader.loadInBackground()
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }
}