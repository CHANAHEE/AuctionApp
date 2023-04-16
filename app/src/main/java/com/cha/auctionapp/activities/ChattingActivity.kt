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
import androidx.lifecycle.lifecycleScope
import com.cha.auctionapp.G
import com.cha.auctionapp.adapters.MessageAdapter
import com.cha.auctionapp.adapters.PictureChatAdapter
import com.cha.auctionapp.databinding.ActivityChattingBinding
import com.cha.auctionapp.model.MessageItem
import com.cha.auctionapp.model.PictureItem
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.*


class ChattingActivity : AppCompatActivity() {

    lateinit var binding: ActivityChattingBinding

    lateinit var otherNickname: String
    lateinit var otherID: String
    var otherProfile: Uri? = null

    lateinit var items: MutableList<PictureItem>
    lateinit var messageItem: MutableList<MessageItem>
    lateinit var pictureSelectedItem: MutableList<Uri>
    lateinit var pictureItem: MutableList<Uri>
    var firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        loadMessage()
        //Glide.with(this).load(G.profile).error(R.drawable.default_profile).into(binding.btnSend)
    }

    /*
    *       초기화 작업
    * */
    private fun init(){
        otherNickname = intent.getStringExtra("otherNickname")!!
        otherProfile = intent.data
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
            if(hasFocus) binding.relativeOption.visibility = View.GONE
        }
        createFirebaseCollectionName()

    }


    /*
    *
    *       서버에 메시지 저장.
    *
    * */
    private fun clickSendBtn(){
        if(collectionName == null) return
        var chatRef = firestore.collection(collectionName!!)

        var nickname = G.nickName
        var message = binding.etMsg.text.toString()
        var id = G.userAccount.id
        var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var minute = Calendar.getInstance().get(Calendar.MINUTE)
        var time = if(minute < 10) "$hour : 0$minute" else "$hour : $minute"


        if(pictureSelectedItem.isNotEmpty())
        {
            Log.i("zxcv45","사진 정보가 남아있나? : ${pictureSelectedItem.size.toString()} : ${pictureSelectedItem}")
            uploadPictureToFirestore(pictureSelectedItem,nickname,message,id,time,chatRef)
        }
        else {
            Log.i("zxcv45",pictureSelectedItem.size.toString())
            Log.i("zxcv45","set 시작 ")
            chatRef.document("MSG_${System.currentTimeMillis()}").set(MessageItem(nickname,id,message, time,G.profileImg,pictureSelectedItem)).addOnFailureListener {
                Log.i("zxcv45",it.message.toString())
            }
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
                                         chatRef: CollectionReference){
        val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
        var fileName = ""
        fileName = "$collectionName/${G.userAccount.id}$time/"

        Log.i("123asdfe", "사진 정보 추가: ${pictureSelectedItem.size}")
        for(i in pictureSelectedItem.indices){
            val imgRef: StorageReference =
                firebaseStorage.getReference("${fileName}IMG_$time$i.jpg")
            Log.i("123asdfe", "사진 정보 추가: ${pictureSelectedItem[i]}")
            imgRef.putFile(pictureSelectedItem[i]).addOnSuccessListener(OnSuccessListener<Any?> {
                Log.i("123asdfe", "사진 정보 추가 성공!!: ${pictureSelectedItem[i]}")
                imgRef.downloadUrl.addOnSuccessListener {
                    Log.i("123asdfe", "사진 정보 다운로드URL 성공!!: ${pictureSelectedItem[i]}")
                    var chatRef = firestore.collection(collectionName!!)
                    pictureItem.add(it)
                    //Log.i("123asdfe", "${pictureItem[i]}")
                    if(i == pictureSelectedItem.size - 1) {
                        chatRef.document("MSG_${System.currentTimeMillis()}").set(
                            MessageItem(
                                nickname,
                                id,
                                message,
                                time,
                                G.profileImg,
                                pictureItem
                            )
                        ).addOnFailureListener {
                            Log.i("123asdfe", "사진 정보 추가 실패 : ${it.message}")
                        }
                    }
                }
//                if(i == pictureItem.size-1) {
//
//                    imgRef.downloadUrl.addOnSuccessListener {
//                        var chatRef = firestore.collection(collectionName!!)
//                        pictureItem.add(it)
//                        chatRef.document("MSG_${System.currentTimeMillis()}").set(MessageItem(nickname,id,message, time,G.profileImg,pictureItem)).addOnFailureListener {
//                            Log.i("123asdfe","사진 정보 추가: ${it.message}")
//                        }
//
//                    }
//                }
            })
                .addOnFailureListener(
                    OnFailureListener {
                        Log.i("123asdfe","사진 정보 추가 실패: ${it.message}")
                    })
        }
    }

    /*
    *
    *       메시지 불러오기
    *
    * */
    private fun loadMessage() {
        var chatRef = firestore.collection(collectionName!!)

        chatRef.addSnapshotListener { value, error ->

            var documentChanges = value?.documentChanges ?: return@addSnapshotListener

            for (documentChange in documentChanges) {
                var snapshot = documentChange.document
                var map = snapshot.data

                var nickname = map.get("nickname").toString()
                var id = map.get("id").toString()
                var message = map.get("message").toString()
                var time = map.get("time").toString()
                var profileImage = Uri.parse(map.get("profileImage").toString())
                var image = map.get("image") as MutableList<*>

                for(i in 0 until image.size){
                    pictureItem.add(Uri.parse(image[i].toString()))
                }



                messageItem.add(MessageItem( nickname,id, message, time,profileImage,pictureItem))
                binding.recycler.adapter?.notifyItemInserted(messageItem.size)
                binding.recycler.scrollToPosition(messageItem.size-1)
//                pictureItem.clear()
//                        getFileCount(id,time)
//                        getPictureURLFromFirestore(nickname,id,message,time,profileImage,fileNum)
//                        Glide.with(this@ChattingActivity).load("https://firebasestorage.googleapis.com/v0/b/auctionapp-cha.appspot.com/o/profile%2FIMG_107906160521912199636.jpg?alt=media&token=41c70088-bd6b-49b7-899d-a43e909482be").into(binding.btnOption)

                Log.i("15eeee","${inx} 반복문 종료")
            }
        }
    }
//
//
//    var fileNum = 0
//
//    private suspend fun getFileCount(id: String, time: String) = coroutineScope{
//        Log.i("15eeee","파일카운트 실행")
//        val firebaseStorage = FirebaseStorage.getInstance()
//        val rootRef = firebaseStorage.reference
//        var fileRef = rootRef.child("${collectionName}/$id$time/")
//
//        fileRef.listAll().addOnSuccessListener {task->
//            Log.i("15eeee","파일카운트 비동기 작업 실행")
//            fileNum = task.items.size
//
//            Log.i("15eeee","파일카운트 비동기 작업 종료")
//        }.await()
//    }
//
//    private suspend fun getPictureURLFromFirestore(nickname: String,
//                                                   id: String,
//                                                   message: String,
//                                                   time: String,
//                                                   profileImage: Uri,
//                                                   fileNum: Int) = suspendCoroutine<Boolean>{
//
//
//        Log.i("15eeee","getPicture 실행")
//        val firebaseStorage = FirebaseStorage.getInstance()
//        Log.i("15eeee","getPicture for문 시작 : 1")
//        val rootRef = firebaseStorage.reference
//
//        Log.i("15eeee","getPicture for문 시작 : 2")
//        var fileCount = fileNum
//        Log.i("15eeee","getPicture for문 시작 : 3")
//        var fileRef = rootRef.child("${collectionName}/$id$time/")
//        Log.i("15eeee","getPicture for문 시작 : 4 : $fileNum")
//
//        for(i in 0 until fileNum){
//            Log.i("15eeee","getPicture for문 시작 : $i")
//            var imgRef = rootRef.child("${collectionName}/$id$time/IMG_$time$i.jpg")
//            Log.i("15eeee","getPicture for문 시작 : $imgRef")
//            imgRef.downloadUrl.addOnSuccessListener {
//                uri->
//                Log.i("15eeee","getPicture 비동기 작업 실행")
//                pictureItem.add(uri)
//                Log.i("15eeee","반복문 : ${i}")
//                if(i == fileNum-1){
//                    messageItem.add(MessageItem( nickname,id, message, time,profileImage,pictureItem))
//                    binding.recycler.adapter?.notifyItemInserted(messageItem.size)
//                    Log.i("15eeee","${messageItem[i].image}")
//                    Glide.with(this@ChattingActivity).load(messageItem[i].image).into(binding.btnOption)
//                    pictureItem.clear()
//                }
//                Log.i("15eeee","다운로드 URL : ${pictureItem}")
//                Log.i("15eeee","getPicture 비동기 작업 종료")
//                //it.resume(true)
//
//            }
//        }
//    }

    /*
    *
    *       컬렉션 이름 생성 : 컬렉션이 DB 내 채팅방의 이름 -> 채팅방을 유일하게 구별 가능
    *
    * */
    var collectionName: String? = null
    private fun createFirebaseCollectionName() {
        var compareResult = G.userAccount.id.compareTo(otherID)
        collectionName = if(compareResult > 0) G.userAccount.id + otherID
        else if(compareResult < 0) otherID + G.userAccount.id
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