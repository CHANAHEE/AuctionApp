package com.cha.auctionapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.adapters.MessageAdapter
import com.cha.auctionapp.adapters.PictureChatAdapter
import com.cha.auctionapp.databinding.ActivityChattingBinding
import com.cha.auctionapp.model.ChatRoomInfo
import com.cha.auctionapp.model.MessageItem
import com.cha.auctionapp.model.PictureItem
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.*
import java.io.File
import java.lang.NumberFormatException
import java.text.NumberFormat


class ChattingActivity : AppCompatActivity() {

    var messageIndex = 0
    var lastOtherMessageIndex = 0
    var latitude: String = ""
    var longitude: String = ""
    var firestore = FirebaseFirestore.getInstance()
    var collectionName: String? = null
    var chatRef = firestore.collection("chat")
    lateinit var otherNickname: String
    lateinit var otherID: String
    lateinit var otherProfile: String
    lateinit var binding: ActivityChattingBinding
    lateinit var items: MutableList<PictureItem>
    lateinit var messageItem: MutableList<MessageItem>
    lateinit var pictureSelectedItem: MutableList<Uri>
    lateinit var pictureItem: MutableList<Uri>
    lateinit var chatRoomNameRef: DocumentReference
    lateinit var baseAddr: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }



    /*
    *
    *       초기화 작업
    *
    * */
    private fun init(){
        setOtherInfo()
        setActionBarConfig()
        initItems()
        messageConfig()
        setProductConfig()
        setChatRoomName()
        loadMessage()
    }



    /*
    *       상대방 정보
    * */
    private fun setOtherInfo(){
        otherNickname = intent.getStringExtra("otherNickname")!!
        otherProfile = intent.getStringExtra("otherProfile")!!
        otherID = intent.getStringExtra("otherID")!!
        binding.tvOtherId.text = otherNickname
    }

    /*
    *       액션바 설정
    * */
    private fun setActionBarConfig(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /*
    *       메시지 아이템 초기화
    * */
    private fun initItems(){
        items = mutableListOf()
        messageItem = mutableListOf()
        pictureSelectedItem = mutableListOf()
        pictureItem = mutableListOf()
        binding.recyclerPicture.adapter = PictureChatAdapter(this, items)
        binding.recycler.adapter = MessageAdapter(this,messageItem)
    }

    /*
    *       메시지 보내기 설정
    * */
    private fun messageConfig(){
        binding.btnSend.setOnClickListener { clickSendBtn() }
        binding.btnOption.setOnClickListener { clickOptionBtn() }
        binding.etMsg.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                binding.relativeOption.visibility = View.GONE
                binding.btnOptionCancel.visibility = View.GONE
                binding.btnOption.visibility = View.VISIBLE
            }
        }
    }

    /*
    *       상품 정보 설정
    * */
    private fun setProductConfig(){
        binding.cvProductInfo.setOnClickListener { startActivity(Intent(this,HomeDetailActivity::class.java).putExtra("index",intent.getStringExtra("index"))) }
        binding.tvTitleProductInfo.text = intent.getStringExtra("title")
        binding.tvLocationNameProductInfo.text = intent.getStringExtra("location")
        binding.tvPriceProductInfo.text = intent.getStringExtra("price")
        baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + intent.getStringExtra("image")
        Glide.with(this).load(baseAddr).into(binding.ivMainImgProductInfo)
    }

    /*
    *       채팅방 이름 설정
    * */
    private fun setChatRoomName(){
        createFirebaseCollectionName()
        chatRoomNameRef = firestore.collection("chat").document(collectionName!!)
    }




    /*
    *
    *       서버에 메시지 저장.
    *
    * */
    private fun clickSendBtn(){
        if(isBlankMessage()) return
        if(collectionName == null) return

        var productIndex = intent.getStringExtra("index")
        var nickname = G.nickName
        var message = binding.etMsg.text.toString()
        var id = G.userAccount.id
        var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var minute = Calendar.getInstance().get(Calendar.MINUTE)
        var time = if(minute < 10) "$hour : 0$minute" else "$hour : $minute"
        var location = binding.tvLocationNameChat.text.toString()
        var documentName = System.currentTimeMillis()


        if(pictureSelectedItem.isNotEmpty()){
            uploadPictureToFirestore(
                pictureSelectedItem
                ,nickname
                ,message
                ,id
                ,time
                ,documentName
                ,productIndex!!
            )
        }
        else {
            messageIndex += 1
            chatRef.document(collectionName!!).set(MessageItem(
                productIndex!!,
                nickname,
                id,
                message,
                time,
                G.profileImg,
                pictureSelectedItem,
                0,
                location,
                messageIndex,
                lastOtherMessageIndex,
                otherProfile,
                otherID,
                otherNickname,
                ChatRoomInfo(
                    binding.tvTitleProductInfo.text.toString(),
                    binding.tvLocationNameProductInfo.text.toString(),
                    binding.tvPriceProductInfo.text.toString(),
                    intent.getStringExtra("image") ?: ""
                ),
                latitude,
                longitude
            ))
            chatRoomNameRef.collection(collectionName!!).document("MSG_$documentName").set(MessageItem(
                productIndex,
                nickname,
                id,
                message,
                time,
                G.profileImg,
                pictureSelectedItem,
                0,
                location,
                messageIndex,
                lastOtherMessageIndex,
                otherProfile,
                otherID,
                otherNickname,
                ChatRoomInfo(
                    binding.tvTitleProductInfo.text.toString(),
                    binding.tvLocationNameProductInfo.text.toString(),
                    binding.tvPriceProductInfo.text.toString(),
                    intent.getStringExtra("image") ?: ""
                ),
                latitude,
                longitude))
        }
        resetAfterSendMessage()
    }



    /*
    *
    *       메시지 보낸 후 초기화
    *
    * */
    private fun resetAfterSendMessage(){
        binding.relativeLocationChat.visibility = View.GONE
        binding.cvPicture.visibility = View.GONE
        binding.etMsg.setText("")
        binding.etMsg.clearFocus()
        var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        items.removeAll { items -> true }
    }


    /*
    *
    *       빈 메시지 체크
    *
    * */
    private fun isBlankMessage(): Boolean{
        if(binding.etMsg.text.isBlank() && pictureSelectedItem.size == 0 && binding.tvLocationNameChat.text.isBlank()){
            Snackbar.make(binding.root,"메시지를 입력해주세요",Snackbar.LENGTH_SHORT).show()
            return true
        }
        return false
    }



    /*
    *
    *       사진 업로드
    *
    * */
    private fun uploadPictureToFirestore(pictureSelectedItem: MutableList<Uri>,
                                         nickname: String,
                                         message: String,
                                         id: String,
                                         time: String,
                                         documentName: Long,
                                         productIndex: String){

        val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
        var fileName = "$collectionName/${G.userAccount.id}$time/"

        for(i in 0 until pictureSelectedItem.size){
            val imgRef: StorageReference =
                firebaseStorage.getReference("${fileName}IMG_$time$i.jpg")
            imgRef.putFile(pictureSelectedItem[i]).addOnSuccessListener(OnSuccessListener<Any?> {
                imgRef.downloadUrl.addOnSuccessListener {

                    pictureItem.add(it)
                    if(i == pictureSelectedItem.size - 1) {
                        chatRef.document(collectionName!!).set(MessageItem(
                            productIndex,
                            nickname,
                            id,
                            message,
                            time,
                            G.profileImg,
                            pictureSelectedItem,
                            pictureItem.size,
                            binding.tvLocationNameChat.text.toString(),
                            messageIndex,
                            lastOtherMessageIndex,
                            otherProfile,
                            otherID,
                            otherNickname,
                            ChatRoomInfo(
                                binding.tvTitleProductInfo.text.toString(),
                                binding.tvLocationNameProductInfo.text.toString(),
                                binding.tvPriceProductInfo.text.toString(),
                                baseAddr
                            ),
                            latitude,
                            longitude))
                        chatRoomNameRef.collection(collectionName!!).document("MSG_$documentName").set(
                            MessageItem(
                                productIndex,
                                nickname,
                                id,
                                message,
                                time,
                                G.profileImg,
                                pictureItem,
                                pictureItem.size,
                                binding.tvLocationNameChat.text.toString(),
                                messageIndex,
                                lastOtherMessageIndex,otherProfile,otherID,otherNickname,
                                ChatRoomInfo(
                                    binding.tvTitleProductInfo.text.toString(),
                                    binding.tvLocationNameProductInfo.text.toString(),
                                    binding.tvPriceProductInfo.text.toString(),
                                    baseAddr
                                ),
                                latitude,
                                longitude
                            )
                        ).addOnFailureListener {
                        }
                        pictureItem.clear()
                        pictureSelectedItem.clear()
                    }
                }
            })
                .addOnFailureListener(
                    OnFailureListener {
                    })
        }
    }



    /*
    *
    *       메시지 불러오기
    *
    * */
    private fun loadMessage() {
        firestore.collection("chat").document(collectionName!!).collection(collectionName!!).addSnapshotListener { value, error ->
            var documentChange = value?.documentChanges ?: return@addSnapshotListener
            for(document in documentChange){
                var snapshot = document.document
                var map = snapshot.data
                var productIndex = map.get("productIndex").toString()
                var nickname = map.get("nickname").toString()
                var id = map.get("id").toString()
                var message = map.get("message").toString()
                var time = map.get("time").toString()
                var profileImage = Uri.parse(map.get("profileImage").toString())
                var image = map.get("image") as MutableList<*>
                var imageSize: String? = map.get("imageSize").toString()
                var location = map.get("location").toString()
                var messageIndex = map.get("messageIndex").toString()
                latitude = map.get("latitude").toString()
                longitude = map.get("longitude").toString()

                for(i in 0 until image.size){
                    pictureItem.add(Uri.parse(image[i].toString()))
                }
                var newPictureItem = pictureItem.toMutableList()

                try{
                    messageItem.add(MessageItem(
                        productIndex,
                        nickname,
                        id,
                        message,
                        time,
                        profileImage,
                        newPictureItem,
                        imageSize?.toInt() ?: 0,location,
                        messageIndex?.toInt() ?: 0,
                        lastOtherMessageIndex,
                        otherProfile,
                        otherID,
                        otherNickname,
                        ChatRoomInfo(
                            binding.tvTitleProductInfo.text.toString(),
                            binding.tvLocationNameProductInfo.text.toString(),
                            binding.tvPriceProductInfo.text.toString(),
                            baseAddr
                        ),
                        latitude,
                        longitude))
                }catch (e: NumberFormatException){
                    messageItem.add(MessageItem(
                        productIndex,
                        nickname,
                        id,
                        message,
                        time,
                        profileImage,
                        newPictureItem,
                        0,
                        location,
                        0,
                        lastOtherMessageIndex,
                        otherProfile,
                        otherID,
                        otherNickname,
                        ChatRoomInfo(
                            binding.tvTitleProductInfo.text.toString(),
                            binding.tvLocationNameProductInfo.text.toString(),
                            binding.tvPriceProductInfo.text.toString(),
                            baseAddr
                        ),
                        latitude,
                        longitude))
                }
                pictureItem.clear()
            }

            binding.recycler.adapter?.notifyItemInserted(messageItem.size)
            binding.recycler.scrollToPosition(messageItem.size-1)
        }
    }


    /*
    *
    *       컬렉션 이름 생성 : 컬렉션이 DB 내 채팅방의 이름 -> 채팅방을 유일하게 구별 가능
    *
    * */
    private fun createFirebaseCollectionName() {
        var compareResult = G.userAccount.id.compareTo(otherID)
        collectionName = if(compareResult > 0) G.userAccount.id + otherID + intent.getStringExtra("index")
        else if(compareResult < 0) otherID + G.userAccount.id + intent.getStringExtra("index")
        else null
    }


    /*
    *
    *       옵션 버튼 클릭 : 사진, 장소
    *       키보드 이벤트 처리
    * */
    private fun clickOptionBtn() {
        binding.relativeOption.visibility = View.VISIBLE
        binding.btnOption.visibility = View.GONE
        binding.btnOptionCancel.visibility = View.VISIBLE
        binding.etMsg.clearFocus()

        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMsg.windowToken, 0)

        binding.relativeCamera.setOnClickListener { clickImageBtn() }
        binding.relativeLocation.setOnClickListener { clickLocationBtn() }
        binding.btnOptionCancel.setOnClickListener { clickOptionCancelBtn() }
    }
    private fun clickOptionCancelBtn() {
        binding.relativeOption.visibility = View.GONE
        binding.btnOption.visibility = View.VISIBLE
        binding.btnOptionCancel.visibility = View.GONE

        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etMsg,InputMethodManager.SHOW_IMPLICIT)
    }

    private fun clickImageBtn() {
        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            .apply {
                type = "image/*"
            }
            .putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX,5-items.size)

        launcher.launch(intent)
    }

    var launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                pictureItem.clear()
                var clipData = it.data?.clipData!!
                var size = clipData.itemCount
                var file = mutableListOf<File>()
                for(i in 0 until size){
                    file.add(File(getFilePathFromUri(clipData.getItemAt(i).uri)!!))
                }

                val uiScope = CoroutineScope(Dispatchers.Main)
                uiScope.launch {
                    for(i in 0 until size){
                        val imageCompress = Compressor.compress(this@ChattingActivity,file[i])
                        val cacheUri = FileProvider.getUriForFile(this@ChattingActivity,"com.cha.auctionapp.fileprovider",imageCompress)
                        pictureSelectedItem.add(cacheUri)
                        items.add(PictureItem(cacheUri))
                    }
                    binding.cvPicture.visibility = View.VISIBLE
                    binding.recyclerPicture.adapter?.notifyDataSetChanged()
                }
            }
        })


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


    @SuppressLint("SuspiciousIndentation")
    private fun clickLocationBtn(){
        var intent = Intent(this,SelectPositionActivity::class.java)
        launcherLocationSelect.launch(intent)
    }

    var launcherLocationSelect: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()
    ) {
        when(it.resultCode){
            RESULT_OK->{
                binding.relativeLocationChat.visibility = View.VISIBLE
                binding.tvLocationNameChat.text = it.data?.getStringExtra("position")
                latitude = it.data?.getStringExtra("latitude")!!
                longitude = it.data?.getStringExtra("longitude")!!

                binding.btnCancelChat.setOnClickListener {
                    binding.relativeLocationChat.visibility = View.GONE
                    binding.tvLocationNameChat.text = ""
                }
            }
        }
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