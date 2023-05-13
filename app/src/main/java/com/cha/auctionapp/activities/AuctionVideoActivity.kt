package com.cha.auctionapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_180
import android.view.Surface.ROTATION_90
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.AspectRatio
import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.AspectRatio.Ratio
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.impl.CaptureConfig
import androidx.camera.core.impl.OptionsBundle
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityAuctionVideoBinding
import com.cha.auctionapp.model.PictureItem
import com.google.android.material.snackbar.Snackbar
import com.iammert.library.cameravideobuttonlib.CameraVideoButton
import okhttp3.internal.notifyAll
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AuctionVideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionVideoBinding
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    /*
    *
    *       초기화 작업
    *
    * */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun init(){
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // Set up the listeners for take photo and video capture buttons
        binding.btnChange.setOnClickListener { switchCamera() }
        binding.civAlbum.setOnClickListener{ clickAlbum() }
        cameraExecutor = Executors.newSingleThreadExecutor()
        checkCameraPermission()
        //requestPermission()
        setUpvideoCaptureButton()
        setLatestImage()
    }







    /*
    *
    *       비디오 캡처 버튼 세팅
    *
    * */
    private fun setUpvideoCaptureButton(){
        binding.fab.setVideoDuration(15000)
        binding.fab.enableVideoRecording(true)
        binding.fab.actionListener = object : CameraVideoButton.ActionListener{
            override fun onDurationTooShortError() {
            }

            override fun onEndRecord() {
                captureVideo()
                binding.btnNext.visibility = View.VISIBLE
            }

            override fun onSingleTap() {
            }

            override fun onStartRecord() {
                binding.btnNext.visibility = View.GONE
                captureVideo()
            }
        }
    }



    /*
    *
    *       영상 촬영 시작
    *
    * */
    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return
        binding.fab.isEnabled = false
        if(isNotNullRecordingSession()) return


        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(setUpMediaConfig())
            .build()

        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@AuctionVideoActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> binding.fab.apply { isEnabled = true }

                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {

                            binding.btnNext.setOnClickListener {
                                startActivity(Intent(this,
                                AuctionVideoCompleteActivity::class.java)
                                .putExtra("video",recordEvent.outputResults.outputUri))

                                it.visibility = View.GONE
                            }

                        } else {
                            recording?.close()
                            recording = null
                        }

                        binding.fab.apply { isEnabled = true }
                    }
                }
            }
    }



    /*
    *
    *       진행중인 recording 체크 : 있으면 return
    *
    * */
    private fun isNotNullRecordingSession(): Boolean{
        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return true
        }
        return false
    }



    /*
    *
    *       Video 파일의 이름, MIME TYPE 세팅
    *
    * */
    private fun setUpMediaConfig(): ContentValues{
        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }
        return contentValues
    }



    /*
    *
    *       카메라 시작
    *
    * */
    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            videoCapture = VideoCapture.withOutput(setRecorder())

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, setPreview(), videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "동영상 촬영에 실패했습니다.", exc)
            }
        }, ContextCompat.getMainExecutor(this))

    }



    /*
    *
    *       Preview 세팅
    *
    * */
    private fun setPreview(): Preview{
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

        return preview
    }



    /*
    *
    *       Recorder 세팅
    *
    * */
    private fun setRecorder(): Recorder{
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()

        return recorder
    }




    /*
    *
    *       앨범 선택 기능
    *
    * */
    private fun clickAlbum() {
        getVideoFromGallery()
    }
    private fun getVideoFromGallery() {
        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
            type = "video/*"
        }
        launcherPictureSelect.launch(intent)
    }

    var launcherPictureSelect: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                startActivity(Intent(this,
                    AuctionVideoCompleteActivity::class.java)
                    .putExtra("video",it.data?.data))
            }
        })



    /*
    *
    *       앨범 최신 비디오 가져와서 미리보기 보여주기
    *
    * */
    private fun setLatestImage() {

        var projection = arrayOf(
            MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Video.VideoColumns.DATE_TAKEN,
            MediaStore.Video.VideoColumns.MIME_TYPE
        )
        Log.i("albumTest","projection : $projection")
        val cursor = baseContext.contentResolver
            .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                null)

        Log.i("albumTest","projection : $cursor")
        //MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        //MediaStore.Video.VideoColumns.DATE_MODIFIED + " DESC"
        if (cursor!!.moveToFirst()) {
            var latestVideoUri = cursor.getString(1)
            Glide.with(this).load(latestVideoUri).error(R.color.unable).into(binding.ivAlbum)
        }
    }




    /*
    *
    *       카메라 렌즈 방향 변경
    *
    * */
    @RequiresApi(Build.VERSION_CODES.R)
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
    *       오디오와 카메라 퍼미션 체크 및 허용
    *
    * */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkCameraPermission() {
        var permissions = mutableListOf<String>()
        permissions.add(Manifest.permission.CAMERA)
        permissions.add(Manifest.permission.RECORD_AUDIO)
        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        permissions.add(Manifest.permission.READ_MEDIA_VIDEO)

        var checkResult1 = checkSelfPermission(permissions.get(0))
        var checkResult2 = checkSelfPermission(permissions.get(1))
        var checkResult3 = checkSelfPermission(permissions.get(2))
        var checkResult4 = checkSelfPermission(permissions.get(3))

        if(checkResult1 == PackageManager.PERMISSION_DENIED
            || checkResult2 == PackageManager.PERMISSION_DENIED
            || checkResult3 == PackageManager.PERMISSION_DENIED
            || checkResult4 == PackageManager.PERMISSION_DENIED) {

            var stringArr = permissions.toTypedArray()
            launcher.launch(stringArr)
        }else if(allPermissionsGranted()) startCamera(cameraSelector)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    var launcher: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
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
                startCamera(cameraSelector)
                Snackbar.make(binding.root,"카메라,오디오 및 파일 사용이 허용되었습니다.",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
//
//    /*
//    *
//    *       퍼미션 요청
//    *
//    * */
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun requestPermission(){
//        // Request camera permissions
//        if (allPermissionsGranted()) {
//            startCamera(cameraSelector)
//        } else {
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}

