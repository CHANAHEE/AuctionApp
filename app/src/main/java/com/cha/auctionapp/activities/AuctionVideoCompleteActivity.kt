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
        binding = com.cha.auctionapp.databinding.ActivityAuctionVideoCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        var videoUri = Uri.parse(intent.getStringExtra("video"))
        Log.i("test123scbv",videoUri.toString())
        var videopath = getRealPathFromUri(videoUri)

        binding.videoview.setVideoPath(videopath)
        var rotation = getImageOrientation(videopath!!)
        Log.i("rotateTest",rotation.toString())
        binding.videoview.setOnPreparedListener {
            binding.videoview.start()
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource("$videopath")
            val width =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            val height =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))

            retriever.release()
            Log.i("videoTest3","$width : $height")
        }

        binding.videoview.setOnCompletionListener { binding.videoview.start() }

        binding.btnComplete.setOnClickListener { startActivity(Intent(this,AuctionEditActivity::class.java).putExtra("video",videoUri.toString())) }
        binding.btnBack.setOnClickListener { finish() }

    }

//    private fun init() {
//        var exoPlayer: ExoPlayer = ExoPlayer.Builder(this).build()
//        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
//        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
//
//
//        var videoUri = Uri.parse(intent.getStringExtra("video"))
//        var mediaItem: MediaItem = MediaItem.fromUri(videoUri)
//        binding.videoview.player = exoPlayer
//        exoPlayer.prepare()
//        exoPlayer.setMediaItem(mediaItem)
//        exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
//
//
//    }

    fun getImageOrientation(path: String): Int {
        var rotation = 0
        try {
            val exif = ExifInterface(path)
            val rot: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            rotation = if (rot == ExifInterface.ORIENTATION_ROTATE_90) {
                0
            } else if (rot == ExifInterface.ORIENTATION_ROTATE_180) {
                0
            } else if (rot == ExifInterface.ORIENTATION_ROTATE_270) {
                0
            } else {
                0
            }
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return rotation
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


    inner class MotiveVideoView : VideoView {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }
}