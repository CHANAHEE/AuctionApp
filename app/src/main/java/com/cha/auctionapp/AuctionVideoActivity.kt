package com.cha.auctionapp

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cha.auctionapp.databinding.ActivityAuctionVideoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.Locale

class AuctionVideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCameraPermission()
        binding.fab.setOnClickListener { clickCaptureBtn() }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    /*
    *
    *       프리뷰 기능
    *
    * */
    lateinit var imageCapture: ImageCapture
    private fun startCamera() {
        val listenableFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
        listenableFuture.addListener(Runnable {
                                              kotlin.run {
                                                  var cameraProvider = listenableFuture.get()
                                                  var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                                  var preview = Preview.Builder().build()
                                                  preview.setSurfaceProvider(binding.previewView.surfaceProvider)
                                                  imageCapture = ImageCapture.Builder().build()
                                                  cameraProvider.bindToLifecycle(this@AuctionVideoActivity,cameraSelector,preview,imageCapture)
                                              }

        },ContextCompat.getMainExecutor(this))
    }


    /*
    *
    *       캡처 버튼 이벤트
    *
    * */
    private fun clickCaptureBtn() {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
        val fileName = sdf.format(System.currentTimeMillis())

        var contentValue: ContentValues = ContentValues()
        contentValue.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        contentValue.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) contentValue.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            "Pictures/CameraX-Image"
        )

        val options = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue
        ).build()

        imageCapture.takePicture(
            options,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(this@AuctionVideoActivity, "촬영 성공", Toast.LENGTH_SHORT).show()
                    binding.tv.setText(outputFileResults.savedUri.toString())
                    Glide.with(this@AuctionVideoActivity).load(outputFileResults.savedUri).into(binding.civ)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@AuctionVideoActivity, "Error: $exception", Toast.LENGTH_SHORT)
                        .show()
                }
            })
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

        if(checkResult1 == PackageManager.PERMISSION_DENIED || checkResult2 == PackageManager.PERMISSION_DENIED){
            var stringArr = permissions.toTypedArray()
            launcher.launch(stringArr)
        }
    }
    var launcher: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var keys: Set<String> = it.keys
        for(key in keys){
            var value = it.get(key) ?: false
            if(!value) Snackbar.make(binding.root,"$key 퍼미션이 허용되었습니다.",Snackbar.LENGTH_SHORT).show()
        }
    }
}