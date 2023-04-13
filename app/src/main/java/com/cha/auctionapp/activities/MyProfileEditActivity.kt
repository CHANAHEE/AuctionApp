package com.cha.auctionapp.activities


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityMyProfileEditBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyProfileEditActivity : AppCompatActivity() {

    lateinit var binding : ActivityMyProfileEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        Log.i("alertdl","다이얼로그 띄우기")
        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.civProfile.setOnClickListener { clickProfileImage() }

        binding.civProfile.tag = DEFAULT_PROFILE

        loadProfileFromFirestore(G.userAccount.id)
        binding.etNickname.setText(G.nickName)
    }

    val DEFAULT_PROFILE = 0
    val CHANGED_PROFILE = 1



    /*
    *
    *       프로필 사진 변경 & 런처
    *
    * */
    private fun clickProfileImage() {
        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        pickLauncher.launch(intent)
    }
    var pickLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK) {
            Glide.with(this).load(getFilePathFromUri(it.data?.data!!)!!).into(binding.civProfile)
            G.profileImg = it.data?.data!!
            binding.civProfile.tag = CHANGED_PROFILE

            val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
            val fileName = "profile/IMG_" + G.userAccount.id + ".jpg"
            val imgRef: StorageReference =
                firebaseStorage.getReference("$fileName")

            imgRef.putFile(it.data?.data!!).addOnSuccessListener(OnSuccessListener<Any?> {

            })
                .addOnFailureListener(
                    OnFailureListener {
                        Log.i("test1212",it.toString())

                    })
        }
    }
    /*
    *
    *       기본 프로필 설정
    *
    * */
    private fun loadProfileFromFirestore(profile: String){
        Log.i("test010101", "$profile  hhh")
        val firebaseStorage = FirebaseStorage.getInstance()

        // 저장소의 최상위 위치를 참조하는 참조객체를 얻어오자.
        val rootRef = firebaseStorage.reference

        // 읽어오길 원하는 파일의 참조객체를 얻어오자.
        val imgRef = rootRef.child("profile/IMG_$profile.jpg")
        Log.i("test12344","${imgRef} : ${G.userAccount.id}")
        if (imgRef != null) {
            // 파일 참조 객체로 부터 이미지의 다운로드 URL 얻어오자.
            imgRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri?> {

                override fun onSuccess(p0: Uri?) {
                    Glide.with(this@MyProfileEditActivity).load(p0).error(R.drawable.default_profile).into(binding.civProfile)
                }
            }).addOnFailureListener {
                Log.i("test12344",it.toString())
            }
        }
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
    *       완료 버튼
    *
    * */
    private fun clickCompleteBtn() {

        certifyNickname()
        if(isExistNickname) return

        if(binding.etNickname.text.length < 3){
            AlertDialog.Builder(this).setMessage("닉네임을 3글자 이상으로 설정해주세요").show()
            return
        }else {
            var dialog = AlertDialog.Builder(this).setMessage("프로필을 설정 하시겠습니까?").setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which ->
                    updateProfile()
                    finish()
                }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  }).create()

            dialog.show()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.brand,theme))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.brand,theme))
        }
    }


    /*
    *
    *       변경된 프로필 정보 G 클래스 저장
    *
    * */
    private fun updateProfile(){
        G.nickName = binding.etNickname.text.toString()
        if(binding.civProfile.tag == DEFAULT_PROFILE) G.profileImg = getURLForResource(R.drawable.default_profile)

        if(intent.getStringExtra("Login") == "Login"){
            setResult(RESULT_OK,getIntent())
            launcherActivity.launch(Intent(this,SetUpMyPlaceListActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
        }
        else setResult(RESULT_OK,intent)
    }


    /*
    *
    *       닉네임 중복체크
    *
    * */
    var isExistNickname = true
    private fun certifyNickname(){
        var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firebase.collection("user")

        userRef.whereEqualTo("nickname",binding.etNickname.text.toString()).get().addOnSuccessListener {
            if(it.documents.size > 0) {
                Snackbar.make(binding.root,"이미 있는 닉네임 입니다.",Snackbar.LENGTH_SHORT).show()
                isExistNickname = true
            } else isExistNickname = false
        }
    }


    /*
    *
    *       동네 설정 후 종료 시킬 런처
    *
    * */
    val launcherActivity: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK) finish()
        })


    /*
    *
    *       기본 프로필 이미지 : drawable -> Uri
    *
    * */
    private fun getURLForResource(resId: Int): Uri {
        return Uri.parse("android.resource://" + (R::class.java.getPackage()?.getName()) + "/" + resId)
    }

    /*
    *
    *       뒤로 가기 버튼
    *
    * */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}