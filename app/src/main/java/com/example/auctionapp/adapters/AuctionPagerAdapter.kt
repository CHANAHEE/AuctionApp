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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.R
import com.example.auctionapp.activities.MainActivity
import com.example.auctionapp.databinding.FragmentAuctionBinding
import com.example.auctionapp.databinding.FragmentAuctionDetailBottomSheet2Binding
import com.example.auctionapp.databinding.FragmentAuctionDetailBottomSheetBinding
import com.example.auctionapp.databinding.RecyclerAuctionItemBinding
import com.example.auctionapp.model.AuctionPagerItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class AuctionPagerAdapter(var context: Context,var items: MutableList<AuctionPagerItem>) : Adapter<AuctionPagerAdapter.VH>() {

    lateinit var dialog1: BottomSheetDialog
    lateinit var dialog2: BottomSheetDialog

    inner class VH(var binding: RecyclerAuctionItemBinding) : ViewHolder(binding.root){
        var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerAuctionItemBinding.inflate(LayoutInflater.from(context),parent,false))


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        var item: AuctionPagerItem = items[position]

        holder.binding.tvId.text = item.id
        holder.binding.tvDescription.text = item.description
        Glide.with(context).load(item.image).into(holder.binding.civProfile)


        /*
        *       BottomSheet 1번 구현
        * */
        holder.binding.btnBid.setOnClickListener { clickBidBtn(holder) }


        /*
        *      Exoplayer 구현
        * */
        exoPlayer(item,holder)
    }

    private fun clickBidBtn(holder: VH){
        dialog1 = BottomSheetDialog(context)
        var binding = FragmentAuctionDetailBottomSheetBinding.inflate(LayoutInflater.from(context),holder.binding.containerBottomsheet,false)
        /*
        *       만약, bottomsheet 의 부모뷰가 있다면.. 부모뷰를 제거해주자
        *       이건 첫번째 bottomsheet 에서 취소버튼을 누른뒤, 다시 입찰했을 때를 위한 작업!
        *       만일, 입찰하기 까지 눌렀다면 다이얼로그 입찰을 눌렀을 떄, 첫번째 bottomsheet 는 없어지므로..
        * */
        if(binding.root.parent != null) ((binding.root.parent) as ViewGroup).removeView(binding.root)

        /*
        *       dialog 를 띄우는 작업
        * */
        dialog1.setContentView(binding.root)
        dialog1.show()

        /*
        *       bottomsheet1 에 들어있는 뷰들의 리스너들
        * */
        binding.btnCancel.setOnClickListener { clickBtnCancel(binding) }
        binding.btnPrice1.setOnClickListener { clickBtnPrice(it,binding) }
        binding.btnPrice2.setOnClickListener { clickBtnPrice(it,binding) }
        binding.btnPrice3.setOnClickListener { clickBtnPrice(it,binding) }

        /*
        *       bottomsheet1 입찰 버튼 클릭 리스너
        * */
        binding.btnBidBottomsheet1.setOnClickListener {
            binding.btnBidBottomsheet1.isEnabled = false
            binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A1A0A0"))
            dialog1.dismiss()
            clickBtnBidBottomSheet1(holder)
        }
    }

    private fun changeBtnEnable(binding: FragmentAuctionDetailBottomSheetBinding){
        binding.btnBidBottomsheet1.isEnabled = true
        binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF96DB8A"))
    }
    private fun clickBtnCancel(binding: FragmentAuctionDetailBottomSheetBinding){
        dialog1.dismiss()
        binding.btnBidBottomsheet1.isEnabled = false
        binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A1A0A0"))
    }
    private fun clickBtnPrice(it: View,binding: FragmentAuctionDetailBottomSheetBinding){
        when(it){
            binding.btnPrice1->{
                binding.tvPrice.text = binding.btnPrice1.text
                changeBtnEnable(binding)
            }
            binding.btnPrice2->{
                binding.tvPrice.text = binding.btnPrice2.text
                changeBtnEnable(binding)
            }
            binding.btnPrice3->{
                binding.tvPrice.text = binding.btnPrice3.text
                changeBtnEnable(binding)
            }
        }
    }
    private fun clickBtnBidBottomSheet1(holder: VH){
        var binding = FragmentAuctionDetailBottomSheet2Binding.inflate(LayoutInflater.from(context),holder.binding.containerBottomsheet,false)
        /*
        *       경고문구를 알려줄 수 있는 다이얼로그 하나를 띄우자.
        *       다이얼로그 입찰 버튼을 누르면, bottomsheet2 가 띄워진다.
        * */
        var alertDialog = AlertDialog.Builder(context)
            .setMessage("입찰을 시작한 뒤 취소하면 불이익이 있을 수 있습니다. 입찰하시겠습니까?")
            .setPositiveButton("입찰") { dialog, which ->
                dialog2 = BottomSheetDialog(context)

                /*
                *       두번째 bottomsheet 띄우기
                * */
                dialog2.setContentView(binding.root)
                dialog2.show()

                binding.btnComplete.setOnClickListener {
                    dialog1.dismiss()
                    dialog2.dismiss()
                    ((binding.root.parent) as ViewGroup).removeView(binding.root)
                }
            }.setNegativeButton("취소", OnClickListener { dialog, which ->  }).create()
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.brand,context.theme))
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.brand,context.theme))
    }









    /*
    *
    *       ExoPlayer 구현
    *
    * */


    private fun exoPlayer(item: AuctionPagerItem,holder: VH){

        var mediaItem: MediaItem = MediaItem.fromUri(item.video)
        holder.binding.videoview.player = holder.exoPlayer

        holder.exoPlayer.setMediaItem(mediaItem)
        holder.exoPlayer.prepare()
        holder.exoPlayer.play()
        holder.exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL

        holder.binding.videoview.setOnClickListener {
            when(holder.exoPlayer.isPlaying){
                true->holder.exoPlayer.pause()
                false->holder.exoPlayer.play()
            }
        }
    }


    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        holder.exoPlayer.pause()
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        holder.exoPlayer.play()
    }
}

