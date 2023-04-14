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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.adapters.MessageAdapter
import com.cha.auctionapp.adapters.PictureChatAdapter
import com.cha.auctionapp.databinding.ActivityChattingBinding
import com.cha.auctionapp.model.MessageItem
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.model.PictureMessageItem
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class ChattingActivity : AppCompatActivity() {

    lateinit var binding: ActivityChattingBinding

    lateinit var otherNickname: String
    lateinit var otherID: String
    var otherProfile: Uri? = null

    lateinit var items: MutableList<PictureItem>
    lateinit var messageItem: MutableList<MessageItem>

    var firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
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

        binding.btnSend.setOnClickListener { clickSendBtn() }
        binding.btnOption.setOnClickListener { clickOptionBtn() }
        binding.etMsg.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.relativeOption.visibility = View.GONE
        }
        createFirebaseCollectionName()
        loadMessage()
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
        var image = items
        var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var minute = Calendar.getInstance().get(Calendar.MINUTE)
        var time = if(minute < 10) "$hour : 0$minute" else "$hour : $minute"

        chatRef.document("MSG_${System.currentTimeMillis()}").set(MessageItem(image,nickname,id,message, time,G.profileImg))

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
    *       메시지 불러오기
    *
    * */
    private fun loadMessage() {
        var chatRef = firestore.collection(collectionName!!)

        chatRef.addSnapshotListener { value, error ->
            var documentChanges = value?.documentChanges ?: return@addSnapshotListener
            for(documentChange in documentChanges){

                var snapshot = documentChange.document
                var map = snapshot.data

                var nickname = map.get("nickname").toString()
                var id = map.get("id").toString()
                var message = map.get("message").toString()
                var time = map.get("time").toString()
                var imageUri = (map.get("image") as MutableList<*>).mapNotNull { it as Map<*,*> }.map { it["uri"].toString() }

                var image: MutableList<PictureItem> = mutableListOf()
                for(i in imageUri.indices) image.add(PictureItem(Uri.parse(imageUri[i])))

                messageItem.add(MessageItem(image, nickname,id, message, time,Uri.parse(map.get("profileImage").toString())))

                Log.i("messageItem", messageItem.get(0).image.size.toString())
                binding.recycler.adapter?.notifyItemInserted(messageItem.size)
                binding.recycler.scrollToPosition((binding.recycler.adapter as MessageAdapter).itemCount - 1)
            }
        }
    }

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
                    items.add(PictureItem(clipData.getItemAt(i).uri))
                    Log.i("1eee","${clipData.getItemAt(i).uri}")
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