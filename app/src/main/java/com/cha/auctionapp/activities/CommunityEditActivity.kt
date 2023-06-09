package com.cha.auctionapp.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.loader.content.CursorLoader
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PictureAdapter
import com.cha.auctionapp.databinding.ActivityCommunityEditBinding
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.material.snackbar.Snackbar
import id.zelory.compressor.Compressor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CommunityEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommunityEditBinding
    lateinit var items: MutableList<PictureItem>

    var latitude: String = ""
    var longitude: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initial()
    }


    /*
    *
    *       초기화 작업
    *
    * */
    private fun initial(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        items = mutableListOf()
        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.relative1.setOnClickListener { clickPicture() }
        binding.relative2.setOnClickListener { clickLocation() }
        binding.recycler.adapter = PictureAdapter(this, items)
    }


    /*
    *
    *     DB 에 커뮤니티 글 저장 ( 제목, 내용, 사진, 장소, 작성자 id, 작성자 동네, 작성자 프로필 사진)
    *
    * */
    private fun clickCompleteBtn(){
        if(isBlankData()) return

        var title = binding.etTitle.text.toString()
        var description = binding.etDecription.text.toString()
        var placeInfo = binding.tvLocationNameCommunityEdit.text.toString()

        var dataPart: HashMap<String,String> = hashMapOf()
        dataPart.put("title",title)
        dataPart.put("description",description)
        dataPart.put("placeinfo",placeInfo)
        dataPart.put("nickname", G.nickName)
        dataPart.put("location",G.location)
        dataPart.put("id",G.userAccount.id)
        dataPart.put("latitude",latitude)
        dataPart.put("longitude",longitude)

        var fileImagePart: MutableList<MultipartBody.Part> = mutableListOf()
        var imagePath = mutableListOf<String>()
        for(i in 0 until items.size){
            imagePath.add(getFilePathFromUri(items[i].uri)!!)
        }
        var coroutine = GlobalScope.launch {
            for(i in 0 until items.size){
                val file: File = File(imagePath[i])
                val imageCompressed = Compressor.compress(this@CommunityEditActivity,file)
                val body = imageCompressed.asRequestBody("image/*".toMediaTypeOrNull())
                fileImagePart.add(i, MultipartBody.Part.createFormData("image${i}",imageCompressed.name,body))
            }
        }

        runBlocking { coroutine.join() }


        /*
        *       Retrofit 작업 시작
        * */
        var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        var retrofitService = retrofit.create(RetrofitService::class.java)
        var call: Call<String> = retrofitService.postDataToServerForCommunityFragment(dataPart,fileImagePart)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                finish()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.", Snackbar.LENGTH_SHORT)
            }
        })
    }

    /*
    *
    *       Uri -> File 변환
    *
    * */
    fun getFilePathFromUri(uri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(
            this,
            uri!!, proj, null, null, null
        )
        val cursor = loader.loadInBackground()
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }

    /*
    *
    *       Data 체크
    *
    * */
    private fun isBlankData(): Boolean{

        if(binding.etTitle.text.toString().isBlank()) {
            binding.etTitle.requestFocus()
            Snackbar.make(binding.root,"제목을 입력해주세요",Snackbar.LENGTH_SHORT).show()
            return true
        }else if(binding.etDecription.text.toString().isBlank()){
            binding.etDecription.requestFocus()
            Snackbar.make(binding.root,"내용을 입력해주세요",Snackbar.LENGTH_SHORT).show()
            return true
        }
        else return false
    }


    /*
    *
    *       사진 선택 버튼
    *
    * */
    private fun clickPicture() {
        binding.etTitle.clearFocus()
        binding.etDecription.clearFocus()
        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            .putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX,5-items.size)
            .apply {
                type = "image/*"
            }
        launcher.launch(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    var launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                var clipData = it.data?.clipData!!
                var size = clipData.itemCount

                for(i in 0 until size){
                    items.add(PictureItem(clipData.getItemAt(i).uri))
                }
                binding.recycler.adapter?.notifyDataSetChanged()
            }
        })


    /*
    *
    *       장소 선택 버튼
    *
    * */
    private fun clickLocation() {
        if(binding.relativeLocation.visibility == View.VISIBLE){
            Snackbar.make(binding.root,"장소는 1개만 추가가 가능합니다",Snackbar.LENGTH_SHORT).show()
            return
        }
        binding.etTitle.clearFocus()
        binding.etDecription.clearFocus()
        var intent = Intent(this,SelectPositionActivity::class.java)
        launcherLocationSelect.launch(intent)
    }

    var launcherLocationSelect: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()
    ) {
        when(it.resultCode){
            RESULT_OK->{
                binding.relativeLocation.visibility = View.VISIBLE
                binding.tvLocationNameCommunityEdit.text = it.data?.getStringExtra("position")
                binding.btnCancelCommunityEdit.setOnClickListener {
                    binding.relativeLocation.visibility = View.GONE
                    binding.tvLocationNameCommunityEdit.text = ""
                }
                latitude = it.data?.getStringExtra("latitude")!!
                longitude = it.data?.getStringExtra("longitude")!!
            }
        }
    }


    /*
    *
    *       뒤로 가기 버튼
    *
    * */
    override fun onSupportNavigateUp(): Boolean {
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