package com.cha.auctionapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityAuctionVideoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class AuctionVideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        binding.fab.setOnClickListener {
            startRecording()
            stopRecording()
        }
        binding.btnChange.setOnClickListener { switchCamera() }

        checkCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        startCamera(cameraSelector)
    }

    /*
    *
    *       프리뷰 기능
    *
    * */
//    lateinit var imageCapture: ImageCapture
//    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//    private fun startCamera(cameraSelector: CameraSelector) {
//        val listenableFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
//        listenableFuture.addListener(Runnable {
//                                              kotlin.run {
//                                                  var cameraProvider = listenableFuture.get()
//                                                  cameraProvider.unbindAll()
//                                                  var preview = Preview.Builder().build()
//                                                  preview.setSurfaceProvider(binding.previewView.surfaceProvider)
//                                                  imageCapture = ImageCapture.Builder().build()
//                                                  cameraProvider.bindToLifecycle(this@AuctionVideoActivity,cameraSelector,preview,imageCapture)
//                                              }
//
//        },ContextCompat.getMainExecutor(this))
//    }
    lateinit var videoCapture: VideoCapture
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    @SuppressLint("RestrictedApi")
    private fun startCamera(cameraSelector: CameraSelector) {
        val listenableFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
        listenableFuture.addListener(Runnable {
            kotlin.run {
                var cameraProvider = listenableFuture.get()
                cameraProvider.unbindAll()
                var preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)
                videoCapture = VideoCapture.Builder().setTargetRotation(binding.previewView.display.rotation).build()
                cameraProvider.bindToLifecycle(this@AuctionVideoActivity,cameraSelector,preview,videoCapture)
            }

        },ContextCompat.getMainExecutor(this))
    }
    /*
    *
    *       카메라 렌즈 방향 변경
    *
    * */
    private fun switchCamera(){
        cameraSelector = if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }else{
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera(cameraSelector)
    }

    /*
    *
    *       캡처 버튼 이벤트
    *
    * */
//    private fun clickCaptureBtn() {
//        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
//        val fileName = sdf.format(System.currentTimeMillis())
//
//        var contentValue: ContentValues = ContentValues()
//        contentValue.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//        contentValue.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) contentValue.put(
//            MediaStore.MediaColumns.RELATIVE_PATH,
//            "Pictures/CameraX-Image"
//        )
//
//        val options = ImageCapture.OutputFileOptions.Builder(
//            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue
//        ).build()
//
//        imageCapture.takePicture(
//            options,
//            ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    Toast.makeText(this@AuctionVideoActivity, "촬영 성공", Toast.LENGTH_SHORT).show()
//                    Glide.with(this@AuctionVideoActivity).load(outputFileResults.savedUri).into(binding.ivAlbum)
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    Toast.makeText(this@AuctionVideoActivity, "Error: $exception", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            })
//    }

    var isRecording = false
    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("RestrictedApi")
    private fun startRecording() {

        if(isRecording) return

        val videoFile = File(externalMediaDirs.firstOrNull(), "video.mp4")

        val option = VideoCapture.OutputFileOptions.Builder(videoFile).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        videoCapture.startRecording(option,this.mainExecutor,object : VideoCapture.OnVideoSavedCallback{
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                isRecording = true
                Glide.with(this@AuctionVideoActivity).load(outputFileResults.savedUri).into(binding.ivAlbum)
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                Snackbar.make(binding.root,"$message : 동영상 촬영에 오류가 생겼습니다.",Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun stopRecording(){
        if(!isRecording) {
            return
        }

        isRecording = false
        videoCapture.stopRecording()
    }

    /*
    *
    *       오디오와 카메라 퍼미션 체크 및 허용
    *
    * */
    private fun checkCameraPermission() {
        var permissions = mutableListOf<String>()
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.RECORD_AUDIO)
        if(Build.VERSION.SDK_INT <= 28) permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        var checkResult1 = checkSelfPermission(permissions.get(0))
        var checkResult2 = checkSelfPermission(permissions.get(1))

        if(checkResult1 == PackageManager.PERMISSION_DENIED
            || checkResult2 == PackageManager.PERMISSION_DENIED){
            var stringArr = permissions.toTypedArray()
            launcher.launch(stringArr)
        }
    }
    var launcher: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var keys: Set<String> = it.keys
        for(key in keys){
            var value = it[key] ?: false

            if(!value) {
                AlertDialog.Builder(this@AuctionVideoActivity)
                    .setMessage("퍼미션이 거부되었습니다. 앱 설정에서 권한을 다시 설정해주세요")
                    .setCancelable(false)
                    .setPositiveButton(
                    "확인"
                ) { dialog, which -> finish() }.show()
            }
            else {
                Snackbar.make(binding.root,"카메라와 오디오 사용이 허용되었습니다.",Snackbar.LENGTH_SHORT).show()


            }
        }
    }



}