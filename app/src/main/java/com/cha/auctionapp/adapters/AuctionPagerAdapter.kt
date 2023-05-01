package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Room
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.AuctionVideoActivity
import com.cha.auctionapp.activities.AuctionDetailActivity
import com.cha.auctionapp.databinding.FragmentAuctionCommentsBottomSheetBinding
import com.cha.auctionapp.databinding.RecyclerAuctionItemBinding
import com.cha.auctionapp.model.AppDatabase
import com.cha.auctionapp.model.AuctionPagerItem
import com.cha.auctionapp.model.CommentsItem
import com.cha.auctionapp.model.CommunityDetailItem
import com.cha.auctionapp.model.MyAuctionFavListItem
import com.cha.auctionapp.model.MyCommunityFavListItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuctionPagerAdapter(var context: Context,var items: MutableList<AuctionPagerItem>) : Adapter<AuctionPagerAdapter.VH>() {



    inner class VH(var binding: RecyclerAuctionItemBinding) : ViewHolder(binding.root){
        var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerAuctionItemBinding.inflate(LayoutInflater.from(context),parent,false))


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        var item: AuctionPagerItem = items[position]

        loadProfileFromFirestore(item,holder)
        loadMyFavItem(position,holder)
        holder.binding.tvDescription.text = item.description

        holder.binding.ibCamera.setOnClickListener { filmingVideo() }
        holder.binding.btnBid.setOnClickListener { clickBidBtn(position) }
        holder.binding.ibFav.setOnClickListener { clickFavoriteBtn(holder,position) }
        holder.binding.ibComments.setOnClickListener { clickCommentsBtn(holder,position) }

        // Exoplayer 구현
        exoPlayer(item,holder)

        // 경매 남은 시간 타이머
        countDown(item,holder)
    }

    /*
    *
    *       프로필 정보 받아오기
    *
    * */
    private fun loadProfileFromFirestore(item: AuctionPagerItem, holder: VH){
        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")
        userRef.document(item.id).get().addOnSuccessListener {
            holder.binding.tvId.text = it.get("nickname").toString()
            Glide.with(context).load(it.get("profileImage")).error(R.drawable.default_profile).into(holder.binding.civProfile)
            return@addOnSuccessListener
        }
    }

    /*
    *
    *       동영상 촬영 클릭 이벤트 : 촬영할 수 있게 해주기
    *
    * */
    private fun filmingVideo() = context.startActivity(Intent(context, AuctionVideoActivity::class.java))




    /*
    *
    *       입찰 버튼 클릭 이벤트
    *
    * */
    private fun clickBidBtn(position: Int) = context.startActivity(Intent(context,AuctionDetailActivity::class.java).putExtra("index",items[position].idx.toString()))



    /*
    *
    *       찜 기능
    *
    * */
    private fun loadMyFavItem(position: Int,holder: VH){
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "fav-database"
        ).build()

        val r = Runnable {
            // Query 를 이용해서 가지고 있는 인덱스의 값이 현재 페이지와 같은지 체크해서 있으면 찜된 목록임.
            var myFavListItem = db.MyAuctionFavListItemDAO().getAll()
            var myFavMutable = myFavListItem.toMutableList()
            val index = items[position].idx
            for(i in 0 until myFavMutable.size){
                if("$index${G.userAccount.id}" == myFavMutable[i].idx) {
                    holder.binding.ibFav.isSelected = true
                    break
                }
            }
        }
        Thread(r).start()
    }



    /*
    *
    *       찜 버튼 이벤트 : 찜을 하면 DB 에 정보를 저장시키고, 관심목록에 추가할 수 있도록 한다.
    *
    * */
    private fun clickFavoriteBtn(holder: VH,position: Int) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "fav-database"
        ).build()
        when(holder.binding.ibFav.isSelected){
            true->{
                holder.binding.ibFav.isSelected = false
                Snackbar.make(holder.binding.root,"관심목록에서 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                deleteMyFavData(db,position)
            }
            else->{
                holder.binding.ibFav.isSelected = true
                Snackbar.make(holder.binding.root,"관심목록에 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
                insertMyFavData(db,position)
            }
        }
    }

    private fun deleteMyFavData(db: AppDatabase,position: Int){
        val r = Runnable {
            db.MyAuctionFavListItemDAO()
                .delete(
                    MyAuctionFavListItem(
                        "${items[position].idx}${G.userAccount.id}",
                        items[position].idx,
                        items[position].title,
                        items[position].location,
                        items[position].description)
                )
        }
        Thread(r).start()
    }

    private fun insertMyFavData(db: AppDatabase,position: Int){
        val r = Runnable{
            db.MyAuctionFavListItemDAO()
                .insert(
                    MyAuctionFavListItem(
                        "${items[position].idx}${G.userAccount.id}",
                        items[position].idx,
                        items[position].title,
                        items[position].location,
                        items[position].description)
                )
        }
        Thread(r).start()
    }




    private fun clickCommentsBtn(holder: VH, position: Int){
        var dialog = BottomSheetDialog(context)
        var binding = FragmentAuctionCommentsBottomSheetBinding.inflate(LayoutInflater.from(context),holder.binding.bottomsheetContainer,false)

        dialog.setContentView(binding.root)
        dialog.show()


        loadCommentsDataFromServer(binding,position)
        binding.btnSend.setOnClickListener { clickSendBtn(binding,position) }
        binding.bottomsheetClose.setOnClickListener { dialog.dismiss() }
    }

    private fun clickSendBtn(binding: FragmentAuctionCommentsBottomSheetBinding,position: Int) {
        // 보낼 일반 String 데이터
        var description = binding.etMsg.text.toString()

        var dataPart: HashMap<String,String> = hashMapOf()
        dataPart.put("idx",items[position].idx.toString())
        dataPart.put("description",description)
        dataPart.put("nickname",G.nickName)
        dataPart.put("location",G.location)
        dataPart.put("id",G.userAccount.id)

        /*
        *       Retrofit 작업 시작
        * */
        var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        var retrofitService = retrofit.create(RetrofitService::class.java)
        var call: Call<String> = retrofitService.postDataToServerForAuctionPagerComments(dataPart)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.i("asdveqrg",response.body().toString())
                /*
                *       서버에 올리는 작업이 성공한다면.. 바로 뿌려줘야 한다. 여기서 다시 서버에 있는 걸 불러와..? 그래야지 다른 사람들도 보지.
                *
                * */
                loadCommentsDataFromServer(binding,position)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.", Snackbar.LENGTH_SHORT).show()
            }
        })
        val imm : InputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMsg.windowToken,0)
        binding.etMsg.setText("")
        binding.etMsg.clearFocus()
    }


    /*
    *
    *       댓글 정보 서버에서 가져오기
    *
    * */
    private fun loadCommentsDataFromServer(binding: FragmentAuctionCommentsBottomSheetBinding,position: Int){
        var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        var retrofitService = retrofit.create(RetrofitService::class.java)
        var call: Call<MutableList<CommentsItem>> = retrofitService.getDataFromServerForAuctionComments(items[position].idx.toString())
        call.enqueue(object : Callback<MutableList<CommentsItem>> {
            override fun onResponse(call: Call<MutableList<CommentsItem>>, response: Response<MutableList<CommentsItem>>) {
                Log.i("acxcbadbrqer",response.body().toString())
                binding.recycler.adapter = AuctionCommentsAdapter(context,response.body()!!)
            }

            override fun onFailure(call: Call<MutableList<CommentsItem>>, t: Throwable) {
                Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.", Snackbar.LENGTH_SHORT).show()
            }
        })
    }






    /*
    *
    *       ExoPlayer 구현
    *
    * */
    private fun exoPlayer(item: AuctionPagerItem,holder: VH){
        var mediaItem: MediaItem = MediaItem.fromUri(item.video)
        holder.binding.videoview.player = holder.exoPlayer
        holder.exoPlayer.prepare()
        holder.exoPlayer.setMediaItem(mediaItem)
        holder.exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
    }


    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        Log.i("pagerExo","${holder.layoutPosition} 번 뷰홀더 Detached")
        holder.exoPlayer.pause()
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        Log.i("pagerExo","${holder.layoutPosition} 번 뷰홀더 Attached")
        holder.exoPlayer.play()
        holder.binding.videoview.setOnClickListener {
            when(holder.exoPlayer.isPlaying){
                true->holder.exoPlayer.pause()
                false->holder.exoPlayer.play()
            }
        }
    }



    /*
    *
    *       게시글을 올린 시간을 기준으로 해야할듯. (12시간 - 게시글 올린 시간) -> 요게 남은 시간
    *       올린 시간을 기준으로 12시간 후에는 게시글을 삭제시키기.
    *
    * */
    private fun countDown(item: AuctionPagerItem,holder: VH){
        var remainTime = 43200000 + item.now.toLong() - System.currentTimeMillis()
        object : CountDownTimer(remainTime,1000) {


            override fun onTick(millisUntilFinished: Long) {
                var hour    = (millisUntilFinished/1000/3600)
                var min     = (millisUntilFinished/1000/60%60)
                var second  = (millisUntilFinished/1000%60)

                var time = String.format("%02d : %02d : %02d",hour,min,second)

                holder.binding.btnBid.text = "입 찰 하 기\n\n${time}"
            }

            override fun onFinish() {
                /*
                *       게시글 삭제
                * */
                holder.binding.btnBid.text = "경매 종료"
                holder.binding.btnBid.backgroundTintList = ColorStateList.valueOf(R.color.unable)
                holder.binding.btnBid.isEnabled = false
            }
        }.start()
    }

}

