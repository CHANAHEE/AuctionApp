package com.cha.auctionapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PictureAdapter
import com.cha.auctionapp.databinding.ActivityAuctionEditBinding
import com.cha.auctionapp.model.MessageItem
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import id.zelory.compressor.Compressor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.DecimalFormat

class AuctionEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionEditBinding
    lateinit var items: MutableList<PictureItem>

    var latitude: String = ""
    var longitude: String = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initial()
    }


    /*
    *
    *       초기화 작업
    *
    * */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initial() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.categoryRelative.setOnClickListener { clickCategory() }
        binding.selectPosRelative.setOnClickListener { clickSelectPos() }

        binding.etPrice.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.ivWon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF000000"))
            else binding.ivWon.imageTintList = ColorStateList.valueOf(Color.parseColor("#4A000000"))
        }
        binding.etPrice.addTextChangedListener(watcher)
    }



    /*
    *
    *       완료 버튼
    *
    * */
    lateinit var dialog: AlertDialog
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun clickCompleteBtn() {

        dialog = AlertDialog.Builder(this).setCancelable(false).setMessage("업로드 중 . . .").create()
        dialog.show()

        // 보낼 일반 String 데이터
        var title = binding.etTitle.text.toString()
        var category = binding.tvCategory.text.toString()
        var price = binding.etPrice.text.toString()
        var description = binding.etDecription.text.toString()
        var location = binding.tvPositionName.text.toString()

        var dataPart: HashMap<String,String> = hashMapOf()
        dataPart.put("title",title)
        dataPart.put("category",category)
        dataPart.put("price",price)
        dataPart.put("description",description)
        dataPart.put("tradingplace",location)
        dataPart.put("nickname", G.nickName)
        dataPart.put("location", G.location)
        dataPart.put("now",System.currentTimeMillis().toString())
        dataPart.put("id", G.userAccount.id)
        dataPart.put("latitude",latitude)
        dataPart.put("longitude",longitude)


        val source: List<Uri> = listOf(intent.getParcelableExtra("video",Uri::class.java)!!)

        VideoCompressor.start(
            context = this, // => This is required
            uris = source, // => Source can be provided as content uris
            isStreamable = false,
            // THIS STORAGE
            sharedStorageConfiguration = SharedStorageConfiguration(
                saveAt = SaveLocation.movies, // => default is movies
                videoName = "compressed_video" // => required name
            ),
            null,
            configureWith = Configuration(
                quality = VideoQuality.VERY_LOW,
                isMinBitrateCheckEnabled = true,
                videoBitrateInMbps = 5, /*Int, ignore, or null*/
                disableAudio = false, /*Boolean, or ignore*/
                keepOriginalResolution = false, /*Boolean, or ignore*/
                videoWidth = 360.0, /*Double, ignore, or null*/
                videoHeight = 480.0 /*Double, ignore, or null*/
            ),
            listener = object : CompressionListener {
                override fun onProgress(index: Int, percent: Float) {
                    // Update UI with progress value
                    runOnUiThread {
                    }
                }

                override fun onStart(index: Int) {
                    Log.i("videoCompress","onStart")
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    Log.i("videoCompress","onSuccess")
                    val file = File(path!!)
                    val videoUri = FileProvider.getUriForFile(this@AuctionEditActivity,"com.cha.auctionapp.fileprovider",file)
                    uploadVideoToStorage(videoUri)

                    dataPart.put("video", videoUri.toString())
                    var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
                    var retrofitService = retrofit.create(RetrofitService::class.java)
                    var call: Call<String> = retrofitService.postDataToServerForAuctionFragment(dataPart)
                    call.enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            startActivity(Intent(this@AuctionEditActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("AuctionDetail","AuctionDetail"))
                            dialog.dismiss()
                            finish()
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.", Snackbar.LENGTH_SHORT).show()
                        }
                    })
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    Log.i("videoCompress","onFailure")
                }

                override fun onCancelled(index: Int) {
                    Log.i("videoCompress","onCancelled")
                }

            }
        )





    }



    /*
    *
    *       Storage 에 비디오 업로드
    *
    * */
    private fun uploadVideoToStorage(source: Uri) {
        val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
        val imgRef: StorageReference =
            firebaseStorage.getReference("video/AVI_${System.currentTimeMillis()}.mp4")
        imgRef.putFile(source).addOnFailureListener(OnFailureListener {
            Snackbar.make(binding.root,"비디오 업로드 실패", Snackbar.LENGTH_SHORT).show()
        })
    }




    /*
    *
    *       카테고리 선택 버튼
    *
    * */
    private fun clickCategory() {
        /*
        *       카테고리 관련 정보 넣어서 다이얼로그 띄우기
        * */
        AlertDialog.Builder(this).setTitle("카테고리 선택").setItems(resources.getStringArray(R.array.category_item),
            DialogInterface.OnClickListener { dialog, which ->
                Log.i("dialogClicked","$dialog $which")
                binding.tvCategory.text = resources.getStringArray(R.array.category_item)[which]
                binding.tvCategory.setTextColor(resources.getColor(R.color.black,theme))
            }).create().show()
    }




    /*
    *
    *       거래 희망 장소 선택
    *
    * */
    private fun clickSelectPos() {
        var intent = Intent(this,SelectPositionActivity::class.java)
        launcherLocationSelect.launch(intent)
    }


    var launcherLocationSelect: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()
    ) {
        when(it.resultCode){
            RESULT_OK->{
                latitude = it.data?.getStringExtra("latitude")!!
                longitude = it.data?.getStringExtra("longitude")!!
                binding.tvPositionName.text = it.data?.getStringExtra("position")
            }
        }
    }



    /*
    *
    *       가격 입력시, 화폐 단위 변경
    *
    * */
    private val decimalFormat = DecimalFormat("#,###")
    private var result: String = ""

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if(!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result){
                result = decimalFormat.format(charSequence.toString().replace(",","").toDouble())
                binding.etPrice.setText(result);
                binding.etPrice.setSelection(result.length);
            }
        }
        override fun afterTextChanged(p0: Editable?) {
        }
    }




    /*
    *
    *       뒤로 가기 버튼
    *
    * */
    override fun onSupportNavigateUp(): Boolean {
        /*
        *       작성중이라면??
        * */
        var dialog = AlertDialog.Builder(this).setMessage("작성 중인 글이 있습니다. 종료하시겠습니까?").setPositiveButton("확인",
            DialogInterface.OnClickListener { dialog, which ->

                finish()
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  }).create()

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.brand,theme))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.brand,theme))

        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        var dialog = AlertDialog.Builder(this).setMessage("작성 중인 글이 있습니다. 종료하시겠습니까?").setPositiveButton("확인",
            DialogInterface.OnClickListener { dialog, which ->

                finish()
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  }).create()

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.brand,theme))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.brand,theme))
    }
}