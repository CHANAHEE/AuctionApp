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
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.*
import java.lang.NumberFormatException
import java.text.NumberFormat


class ChattingActivity : AppCompatActivity() {

    lateinit var binding: ActivityChattingBinding

    var messageIndex = 0
    var lastOtherMessageIndex = 0

    lateinit var otherNickname: String
    lateinit var otherID: String
    lateinit var otherProfile: String

    lateinit var items: MutableList<PictureItem>
    lateinit var messageItem: MutableList<MessageItem>
    lateinit var pictureSelectedItem: MutableList<Uri>
    lateinit var pictureItem: MutableList<Uri>
    var firestore = FirebaseFirestore.getInstance()
    var collectionName: String? = null
    lateinit var chatRoomNameRef: DocumentReference
    var chatRef = firestore.collection("chat")

    lateinit var baseAddr: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        loadMessage()
    }

    /*
    *       초기화 작업
    * */
    private fun init(){
        otherNickname = intent.getStringExtra("otherNickname")!!
        otherProfile = intent.getStringExtra("otherProfile")!!
        Log.i("profilecheck","$otherProfile")
        otherID = intent.getStringExtra("otherID")!!
        binding.tvOtherId.text = otherNickname

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        items = mutableListOf()
        binding.recyclerPicture.adapter = PictureChatAdapter(this, items)
        messageItem = mutableListOf()
        binding.recycler.adapter = MessageAdapter(this,messageItem)
        pictureSelectedItem = mutableListOf()
        pictureItem = mutableListOf()

        binding.btnSend.setOnClickListener { clickSendBtn() }
        binding.btnOption.setOnClickListener { clickOptionBtn() }
        binding.etMsg.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                binding.relativeOption.visibility = View.GONE
                binding.btnOptionCancel.visibility = View.GONE
                binding.btnOption.visibility = View.VISIBLE
            }
        }

        binding.tvTitleProductInfo.text = intent.getStringExtra("title")
        binding.tvLocationNameProductInfo.text = intent.getStringExtra("location")
        binding.tvPriceProductInfo.text = intent.getStringExtra("price")
        baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + intent.getStringExtra("image")
        Glide.with(this).load(baseAddr).into(binding.ivMainImgProductInfo)

        createFirebaseCollectionName()
        Log.i("HELLO",collectionName!!)
        chatRoomNameRef = firestore.collection("chat").document(collectionName!!)
//        getMessageLastIndex()
//        getLastOtherMessageIndex()
    }


    private fun getMessageLastIndex(){
        if(collectionName == null) return
        var chatRef = firestore.collection(collectionName!!)
        chatRef.get().addOnSuccessListener {
            for(document in  it.documents){
                if(document.get("messageIndex").toString() == "") messageIndex = 0
                else messageIndex = document.get("messageIndex").toString().toInt()
            }
            Log.i("4zxc","$messageIndex")
        }
    }


    private fun getLastOtherMessageIndex(){
        if(collectionName == null) return
        var chatRef = firestore.collection(collectionName!!)
        chatRef.orderBy("messageIndex", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    if(document.get("id") != G.userAccount.id){
                        Log.i("4zxc","${document.get("id")} : ${G.userAccount.id}")
                        lastOtherMessageIndex = document.get("messageIndex").toString().toInt()
                        break
                    }
                }
                Log.i("4zxc","$lastOtherMessageIndex")
            }.addOnFailureListener { exception ->
                Log.e("1234aa", "Error getting documents.", exception)
            }
    }
    /*
    *
    *       서버에 메시지 저장.
    *
    * */
//
    private fun clickSendBtn(){
        if(binding.etMsg.text.isBlank() && pictureSelectedItem.size == 0 && binding.tvLocationNameChat.text.isBlank()){
            Snackbar.make(binding.root,"메시지를 입력해주세요",Snackbar.LENGTH_SHORT).show()
            return
        }
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



        if(pictureSelectedItem.isNotEmpty())
        {
            Log.i("pictureIssue","사진 정보가 남아있나? : ${pictureSelectedItem.size.toString()} : ${pictureSelectedItem}")
            uploadPictureToFirestore(pictureSelectedItem,nickname,message,id,time,documentName,productIndex!!)
        }
        else {
            Log.i("pictureIssue","사진이 안남아있네 : ${pictureSelectedItem.size}")

            Log.i("4zxc","$messageIndex")
            messageIndex += 1
            chatRef.document(collectionName!!).set(MessageItem(productIndex!!,nickname,id,message, time,G.profileImg,pictureSelectedItem,0,location,messageIndex ,lastOtherMessageIndex,otherProfile,otherID,otherNickname,
                ChatRoomInfo(
                    binding.tvTitleProductInfo.text.toString(),
                    binding.tvLocationNameProductInfo.text.toString(),
                    binding.tvPriceProductInfo.text.toString(),
                    intent.getStringExtra("image") ?: ""
                )
            ))
            chatRoomNameRef.collection(collectionName!!).document("MSG_$documentName").set(MessageItem(productIndex,nickname,id,message, time,G.profileImg,pictureSelectedItem,0,location,messageIndex ,lastOtherMessageIndex,otherProfile,otherID,otherNickname,
                ChatRoomInfo(
                    binding.tvTitleProductInfo.text.toString(),
                    binding.tvLocationNameProductInfo.text.toString(),
                    binding.tvPriceProductInfo.text.toString(),
                    intent.getStringExtra("image") ?: ""
                )))
            //chatRoomNameRef.set(subCollectionName) -> 필드 인덱스
        }

        binding.relativeLocationChat.visibility = View.GONE
        binding.cvPicture.visibility = View.GONE
        binding.etMsg.setText("")
        binding.etMsg.clearFocus()
        var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        items.removeAll { items -> true }
    }

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
                        chatRef.document(collectionName!!).set(MessageItem(productIndex,nickname,id,message, time,G.profileImg,pictureSelectedItem,0,binding.tvLocationNameChat.text.toString(),messageIndex ,lastOtherMessageIndex,otherProfile,otherID,otherNickname,
                            ChatRoomInfo(
                                binding.tvTitleProductInfo.text.toString(),
                                binding.tvLocationNameProductInfo.text.toString(),
                                binding.tvPriceProductInfo.text.toString(),
                                baseAddr
                            )))
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
                                )
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
//
    private fun loadMessage() {
        Log.i("idid","loadMessage 진입")
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

                for(i in 0 until image.size){
                    pictureItem.add(Uri.parse(image[i].toString()))
                }
                var newPictureItem = pictureItem.toMutableList()

                try{
                    messageItem.add(MessageItem(productIndex, nickname,id, message, time,profileImage,newPictureItem,imageSize?.toInt() ?: 0,location,messageIndex?.toInt() ?: 0,lastOtherMessageIndex,otherProfile,otherID,otherNickname,
                        ChatRoomInfo(
                            binding.tvTitleProductInfo.text.toString(),
                            binding.tvLocationNameProductInfo.text.toString(),
                            binding.tvPriceProductInfo.text.toString(),
                            baseAddr
                        )))
                }catch (e: NumberFormatException){
                    messageItem.add(MessageItem( productIndex, nickname,id, message, time,profileImage,newPictureItem,0,location, 0,lastOtherMessageIndex,otherProfile,otherID,otherNickname,
                        ChatRoomInfo(
                            binding.tvTitleProductInfo.text.toString(),
                            binding.tvLocationNameProductInfo.text.toString(),
                            binding.tvPriceProductInfo.text.toString(),
                            baseAddr
                        )))
                }
                pictureItem.clear()

                Log.i("idid",map.get("id").toString())
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
        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES).putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX,5-items.size)
        launcher.launch(intent)
    }

    var launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                pictureItem.clear()
                var clipData = it.data?.clipData!!
                var size = clipData.itemCount

                for(i in 0 until size){
                    pictureSelectedItem.add(clipData.getItemAt(i).uri)
                    items.add(PictureItem(clipData.getItemAt(i).uri))
                }
                binding.cvPicture.visibility = View.VISIBLE
                binding.recyclerPicture.adapter?.notifyDataSetChanged()
            }
        })
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