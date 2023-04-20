package com.cha.auctionapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
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
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityAuctionVideoBinding
import com.google.android.material.snackbar.Snackbar
import com.iammert.library.cameravideobuttonlib.CameraVideoButton
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit

class AuctionVideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionVideoBinding
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // WORKbench2
        init()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun init(){
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        cameraExecutor = Executors.newSingleThreadExecutor()
        checkCameraPermission()
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera(cameraSelector)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        binding.fab.setVideoDuration(15000)
        binding.fab.enableVideoRecording(true)
        binding.fab.actionListener = object : CameraVideoButton.ActionListener{
            override fun onDurationTooShortError() {
                Log.i("videotest2","듀레이션 짧은 에러")
            }

            override fun onEndRecord() {
                Log.i("videotest2","촬영 종료")
                captureVideo()
                binding.btnNext.visibility = View.VISIBLE
            }

            override fun onSingleTap() {

            }

            override fun onStartRecord() {
                Log.i("videotest2","촬영 시작")
                binding.btnNext.visibility = View.GONE
                captureVideo()
            }

        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.btnChange.setOnClickListener { switchCamera() }
        binding.civAlbum.setOnClickListener{ clickAlbum() }


    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.fab.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

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

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
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
                    is VideoRecordEvent.Start -> {
                        binding.fab.apply {
                            Log.i("videotest2","촬영 시작2")
                            /*
                            *       스탑 버튼 모양으로 바꾸기
                            * */
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"

                            Log.i("videotest2","촬영 종료?")
                            /*
                            *       여기서 다른 액티비티로 Uri 보내주기.
                            *       그러면 그 액티비티에서
                            *
                            * */
                            binding.btnNext.setOnClickListener {

                                startActivity(Intent(this,
                                AuctionVideoCompleteActivity::class.java)
                                .putExtra("video",recordEvent.outputResults.outputUri.toString()))

                                it.visibility = View.GONE
                            }

                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        binding.fab.apply {
                            /*
                            *       시작 버튼 모양으로 바꾸기
                            * */
                            isEnabled = true
                        }
                    }
                }
            }
    }

    /*
    *
    *
    *
    * */


    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }


//            binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
//            preview.targetRotation = ROTATION_90


            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)


            // Select back camera as a default



            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
                val orientationEventListener = object : OrientationEventListener(this) {
                    override fun onOrientationChanged(orientation: Int) {
                        if (orientation == ORIENTATION_UNKNOWN) return

                        val rotation = when (orientation) {
                            in 45 until 135 -> Surface.ROTATION_270
                            in 135 until 225 -> Surface.ROTATION_180
                            in 225 until 315 -> Surface.ROTATION_90
                            else -> Surface.ROTATION_0
                        }

                        preview.targetRotation = rotation
                        videoCapture!!.targetRotation = rotation
                    }
                }

// OrientationEventListener를 등록합니다.
                orientationEventListener.enable()

                Log.i("adiofbnre",preview.camera!!.cameraInfo.sensorRotationDegrees.toString())
            } catch(exc: Exception) {
                Log.e(TAG, "동영상 촬영에 실패했습니다.", exc)
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

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










    /*
    *
    *       앨범 선택 기능
    *
    * */
    private fun clickAlbum() {
        Toast.makeText(this, "앨범 선택 기능. 추후 업데이트 예정", Toast.LENGTH_SHORT).show()
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
        if(Build.VERSION.SDK_INT <= 28) permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        var checkResult1 = checkSelfPermission(permissions.get(0))
        var checkResult2 = checkSelfPermission(permissions.get(1))

        if(checkResult1 == PackageManager.PERMISSION_DENIED
            || checkResult2 == PackageManager.PERMISSION_DENIED){
            var stringArr = permissions.toTypedArray()
            launcher.launch(stringArr)
        }
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
                Snackbar.make(binding.root,"카메라와 오디오 사용이 허용되었습니다.",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

