package com.example.auctionapp.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.R
import com.example.auctionapp.activities.MainActivity
import com.example.auctionapp.databinding.FragmentAuctionDetailBottomSheet2Binding
import com.example.auctionapp.databinding.FragmentAuctionDetailBottomSheetBinding
import com.example.auctionapp.databinding.RecyclerAuctionItemBinding
import com.example.auctionapp.model.AuctionPagerItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class AuctionPagerAdapter(var context: Context,var items: MutableList<AuctionPagerItem>) : Adapter<AuctionPagerAdapter.VH>() {

    inner class VH(var binding: RecyclerAuctionItemBinding) : ViewHolder(binding.root)

    lateinit var bottomSheet1: View
    lateinit var bottomSheet2: View
    lateinit var binding: FragmentAuctionDetailBottomSheetBinding
    lateinit var binding2: FragmentAuctionDetailBottomSheet2Binding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        //bottomSheet1 = (context as MainActivity).layoutInflater.inflate(R.layout.fragment_auction_detail_bottom_sheet,parent,false)
        binding = FragmentAuctionDetailBottomSheetBinding.inflate(LayoutInflater.from(context),parent,false)
        binding2 = FragmentAuctionDetailBottomSheet2Binding.inflate(LayoutInflater.from(context),parent,false)

        bottomSheet1 = binding.root
        bottomSheet2 = binding2.root
        return VH(RecyclerAuctionItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int = items.size






    override fun onBindViewHolder(holder: VH, position: Int) {
        var item: AuctionPagerItem = items[position]

        holder.binding.tvId.text = item.id
        holder.binding.tvDescription.text = item.description
        Glide.with(context).load(item.image).into(holder.binding.civProfile)


        /*
        *       BottomSheet 1번 구현
        * */
        holder.binding.btnBid.setOnClickListener { clickBidBtn() }

        /*
        *      Exoplayer 구현 (4월 3일) 아직 구현중
        * */
        exoPlayer(item,holder)
    }

    private fun clickBidBtn(){
        var dialog: BottomSheetDialog = BottomSheetDialog(context)

        /*
        *       만약, bottomsheet 의 부모뷰가 있다면.. 부모뷰를 제거해주자
        *       이건 첫번째 bottomsheet 에서 취소버튼을 누른뒤, 다시 입찰했을 때를 위한 작업!
        *       만일, 입찰하기 까지 눌렀다면 다이얼로그 입찰을 눌렀을 떄, 첫번째 bottomsheet 는 없어지므로..
        * */
        if(bottomSheet1.parent != null){
            Log.i("BottomSheetDialog", "${bottomSheet1.parent} : bottomsheet.parent 는 null 아님")
            ((bottomSheet1.parent) as ViewGroup).removeView(bottomSheet1)
            Log.i("BottomSheetDialog", "${bottomSheet1.parent} : bottomsheet.parent 는 null")
        }

        /*
        *       dialog 를 띄우는 작업
        * */
        dialog.setContentView(bottomSheet1)
        dialog.show()

        /*
        *       bottomsheet1 에 들어있는 뷰들의 리스너들
        * */
        binding.btnCancel.setOnClickListener {clickBtnCancel(dialog)}
        binding.btnPrice1.setOnClickListener { clickBtnPrice(it) }
        binding.btnPrice2.setOnClickListener { clickBtnPrice(it) }
        binding.btnPrice3.setOnClickListener { clickBtnPrice(it) }

        /*
        *       bottomsheet1 입찰 버튼 클릭 리스너
        * */
        binding.btnBidBottomsheet1.setOnClickListener { clickBtnBidBottomSheet1() }
    }

    private fun clickBtnBidBottomSheet1(){
        /*
        *       경고문구를 알려줄 수 있는 다이얼로그 하나를 띄우자.
        *       다이얼로그 입찰 버튼을 누르면, bottomsheet2 가 띄워진다.
        * */
        Log.i("BottomSheetDialog", "bottomsheet1 입찰 버튼 클릭")
        AlertDialog.Builder(context)
            .setMessage("입찰을 시작한 뒤 취소하면 불이익이 있을 수 있습니다. 입찰하시겠습니까?")
            .setPositiveButton("입찰") { dialog, which ->
                val dialog2 = BottomSheetDialog(context)
                Log.i("BottomSheetDialog", "다이얼로그 입찰 버튼 클릭")

                /*
                *       입찰버튼을 누르면, 첫번째 BottomSheet 는 없애주자.
                * */
                ((bottomSheet1.parent) as ViewGroup).removeView(bottomSheet1)


                /*
                *       두번째 bottomsheet 띄우기
                * */
                dialog2.setContentView(bottomSheet2)
                dialog2.show()

                binding2.btnComplete.setOnClickListener {
                    Log.i("BottomSheetDialog", "bottomsheet2 확인 버튼 클릭")
                    Log.i("BottomSheetDialog", "${bottomSheet1.parent} : bottomsheet.parent 는 null")
                    ((bottomSheet2.parent) as ViewGroup).removeView(bottomSheet2)

                    binding.btnBidBottomsheet1.isEnabled = false
                    binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A1A0A0"))
                }
            }.setNegativeButton("취소", OnClickListener { dialog, which ->  }).show()
    }

    private fun clickBtnCancel(dialog: BottomSheetDialog){
        Log.i("BottomSheetDialog", "bottomsheet1 취소 버튼 클릭")
        ((bottomSheet1.parent) as ViewGroup).removeAllViews()
        binding.btnBidBottomsheet1.isEnabled = false
        binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A1A0A0"))
        //binding.root.removeView(binding.root)
    }


    private fun clickBtnPrice(it: View){
        when(it){
            binding.btnPrice1->{
                binding.tvPrice.text = binding.btnPrice1.text
                binding.btnBidBottomsheet1.isEnabled = true
                binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF96DB8A"))
            }
            binding.btnPrice2->{
                binding.tvPrice.text = binding.btnPrice2.text
                binding.btnBidBottomsheet1.isEnabled = true
                binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF96DB8A"))
            }
            binding.btnPrice3->{
                binding.tvPrice.text = binding.btnPrice3.text
                binding.btnBidBottomsheet1.isEnabled = true
                binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF96DB8A"))
            }
        }
    }

    /*
    *
    *       ExoPlayer 구현
    *
    * */
    private fun exoPlayer(item: AuctionPagerItem,holder: VH){

        //holder.binding.videoview.player = ExoPlayer.Builder(context).build()
        var mediaItem: MediaItem = MediaItem.fromUri(item.video)
        var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

        holder.binding.videoview.player = exoPlayer
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }
}

