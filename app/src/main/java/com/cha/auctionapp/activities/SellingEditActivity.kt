package com.cha.auctionapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.loader.content.CursorLoader
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PictureAdapter
import com.cha.auctionapp.databinding.ActivitySellingEditBinding
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.create
import java.io.File

class SellingEditActivity : AppCompatActivity() {

    lateinit var binding:ActivitySellingEditBinding
    lateinit var items: MutableList<PictureItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellingEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.categoryRelative.setOnClickListener { clickCategory() }
        binding.selectPosRelative.setOnClickListener { clickSelectPos() }
        binding.btnImage.setOnClickListener { clickPicture() }

        binding.etPrice.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.ivWon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF000000"))
            else binding.ivWon.imageTintList = ColorStateList.valueOf(Color.parseColor("#4A000000"))
        }

        items = mutableListOf()
        Log.i("Hello2","${items.size}")
        binding.recycler.adapter = PictureAdapter(this, items)
    }





    /*
    *
    *       사진 선택 버튼 : 앨범에서 선택
    *
    * */
    private fun clickPicture() {
        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES).putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX,10-items.size)
        launcherPictureSelect.launch(intent)
    }

    var launcherPictureSelect: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                var clipData = it.data?.clipData!!
                var size = clipData.itemCount

                for(i in 0 until size){
                    Log.i("Hello2","$i")
                    items.add(PictureItem(clipData.getItemAt(i).uri))
                }
                binding.recycler.adapter?.notifyDataSetChanged()
                binding.btnImage.text = "${items.size} / 10"

                if(items.size == 10) binding.btnImage.visibility = View.GONE
            }
        })


    /*
    *
    *       카테고리 선택 버튼 : 카테고리 관련 정보 넣어서 다이얼로그 띄우기
    *
    * */
    private fun clickCategory() {
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

    lateinit var latitude: String
    lateinit var longitude: String

    var launcherLocationSelect: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()
    ) {
        when(it.resultCode){
            RESULT_OK->{
                Log.i("cha1","${it.data?.getStringExtra("position")}")
                
                latitude = it.data?.getStringExtra("latitude")!!
                longitude = it.data?.getStringExtra("longitude")!!

                binding.tvPositionName.text = it.data?.getStringExtra("position")

            }
        }
    }


    /*
    *
    *       완료 버튼 : DB 에 글 저장
    *
    * */
    private fun clickCompleteBtn() {

        // 보낼 일반 String 데이터
        var title = binding.etTitle.text.toString()
        var category = binding.tvCategory.text.toString()
        var price = binding.etPrice.text.toString()
        var description = binding.etDecription.text.toString()
        var location = binding.tvPositionName.text.toString()

        var dataPart: MutableMap<String,String> = mutableMapOf()
        dataPart.put("title",title)
        dataPart.put("category",category)
        dataPart.put("price",price)
        dataPart.put("description",description)
        dataPart.put("tradingplace",location)
        dataPart.put("nickname",G.nickName)
        dataPart.put("location",G.location)

        Log.i("dataPart",title)
        Log.i("dataPart",category)
        Log.i("dataPart",price)
        Log.i("dataPart",description)
        Log.i("dataPart",location)
        Log.i("dataPart",G.nickName)
        Log.i("dataPart",G.location)


        // 보낼 이미지 데이터들
        var profilePath = File(G.profile.toString())
        var fileProfilePart: MultipartBody.Part? = null

        if (profilePath != null) {
            val body = profilePath.asRequestBody("image/*".toMediaTypeOrNull())
            fileProfilePart = MultipartBody.Part.createFormData("profile", profilePath.name, body)
        }
        Log.i("dataPart",fileProfilePart.toString())
        var fileImagePart: MutableMap<String,MultipartBody.Part> = mutableMapOf()
        for(i in 0 until items.size){
            var imagePath = getFilePathFromUri(items[i].uri)
            val file: File = File(imagePath)
            val body = file.asRequestBody("image/*".toMediaTypeOrNull())
            fileImagePart.put("${i}image",MultipartBody.Part.createFormData("image",file.name,body))
            Log.i("dataPart",fileImagePart["${i}image"].toString())
        }

        Log.i("dataPart",fileProfilePart.toString())

        /*
        *       Retrofit 작업 시작
        * */
//        var retrofit = RetrofitHelper.getRetrofitInstance()
//        var retrofitService = retrofit.create(RetrofitService::class.java)
//        var call: Call<String> = retrofitService.postDataToServer(dataPart,fileImagePart ?: ,fileProfilePart)





        finish()
    }

    /*
    *
    *       Retrofit 으로 사진 파일 전송 시, Uri 주소가 아닌 실제 주소 필요. 변환해주는 함수
    *
    * */
    fun getFilePathFromUri(uri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        Log.i("dataPath",MediaStore.Images.Media.DATA)
        Log.i("dataPath",proj.toString())
        val loader = CursorLoader(
            this,
            uri!!, proj, null, null, null
        )
        val cursor = loader.loadInBackground()
        Log.i("dataPath",cursor.toString())

        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }

    /*
    *
    *       앱 바의 뒤로가기 버튼
    *
    * */
    override fun onSupportNavigateUp(): Boolean {
        /*
        *
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


    /*
    *
    *       네비게이션 바의 뒤로가기 버튼
    *
    * */
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