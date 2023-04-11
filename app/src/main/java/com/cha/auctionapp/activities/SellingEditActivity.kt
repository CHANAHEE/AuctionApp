package com.cha.auctionapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PictureAdapter
import com.cha.auctionapp.databinding.ActivitySellingEditBinding
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class SellingEditActivity : AppCompatActivity() {

    lateinit var binding:ActivitySellingEditBinding
    lateinit var items: MutableList<PictureItem>
    lateinit var flag: String
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

        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")

        userRef.document(G.userAccount.id).get().addOnSuccessListener {

            flag = it.get("profile").toString()


            return@addOnSuccessListener
        }
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
                Log.i("picture123",clipData.getItemAt(0).toString())
                Log.i("picture123",clipData.getItemAt(0).uri.toString())
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

        var dataPart: HashMap<String,String> = hashMapOf()
        dataPart.put("title",title)
        dataPart.put("category",category)
        dataPart.put("price",price)
        dataPart.put("description",description)
        dataPart.put("tradingplace",location)
        dataPart.put("nickname",G.nickName)
        dataPart.put("location",G.location)


//        val firebaseStorage = FirebaseStorage.getInstance()
//
//        // 저장소의 최상위 위치를 참조하는 참조객체를 얻어오자.
//        val rootRef = firebaseStorage.reference
//
//        // 읽어오길 원하는 파일의 참조객체를 얻어오자.
//        val imgRef = rootRef.child( "IMG_" + G.userAccount.id + ".jpg")
//        Log.i("test12344","${imgRef} : ${G.userAccount.id}")
//        if (imgRef != null) {
//            // 파일 참조 객체로 부터 이미지의 다운로드 URL 얻어오자.
//            imgRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri?> {
//
//                override fun onSuccess(p0: Uri?) {
//                    Log.i("test1231","${p0}")
//                    //val file: File = File(p0?.toFile())
//                    Log.i("test1231","${file}")
//                    val body = file.asRequestBody("image/*".toMediaTypeOrNull())
//                    Log.i("test1231","${imgRef.downloadUrl.result}")
//                    var fileProfilePart = MultipartBody.Part.createFormData("profile", file.name, body)
//
//
//
//                }
//            }).addOnFailureListener {
//                Log.i("test12344",it.toString())
//            }
//        }

        // 보낼 이미지 데이터들
        var fileImagePart: MutableList<MultipartBody.Part> = mutableListOf()
        for(i in 0 until items.size){
            var imagePath = getRealPathFromUri(items[i].uri)
            val file: File = File(imagePath)
            val body = file.asRequestBody("image/*".toMediaTypeOrNull())
            fileImagePart.add(i,MultipartBody.Part.createFormData("image${i}",file.name,body))
        }


        /*
        *       Retrofit 작업 시작
        * */
        var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        var retrofitService = retrofit.create(RetrofitService::class.java)
        var call: Call<String> = retrofitService.postDataToServerForHomeFragment(dataPart,fileImagePart)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.i("test2",call.toString())
                Log.i("phpLog",response.body().toString())
                finish()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.i("phpLog",t.message.toString())
                Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.",Snackbar.LENGTH_SHORT)
            }
        })
    }

    /*
    *
    *       Retrofit 으로 사진 파일 전송 시, Uri 주소가 아닌 실제 주소 필요. 변환해주는 함수
    *
    * */
    fun getRealPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return cursor.getString(columnIndex)
        }
        return null
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