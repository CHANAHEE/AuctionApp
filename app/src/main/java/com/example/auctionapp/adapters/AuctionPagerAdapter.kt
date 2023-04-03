package com.example.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.R
import com.example.auctionapp.activities.MainActivity
import com.example.auctionapp.databinding.FragmentAuctionDetailBottomSheetBinding
import com.example.auctionapp.databinding.RecyclerAuctionItemBinding
import com.example.auctionapp.model.AuctionPagerItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class AuctionPagerAdapter(var context: Context,var items: MutableList<AuctionPagerItem>) : Adapter<AuctionPagerAdapter.VH>() {

    inner class VH(var binding: RecyclerAuctionItemBinding) : ViewHolder(binding.root)

    lateinit var bottomSheet1: View
    lateinit var binding: FragmentAuctionDetailBottomSheetBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        //bottomSheet1 = (context as MainActivity).layoutInflater.inflate(R.layout.fragment_auction_detail_bottom_sheet,parent,false)
        binding = FragmentAuctionDetailBottomSheetBinding.inflate(LayoutInflater.from(context),parent,false)
        bottomSheet1 = binding.root
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
        holder.binding.btnBid.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            dialog.setContentView(bottomSheet1)
            dialog.show()

        }




        /*
        *
        *       Exoplayer 구현 (4/3) 아직 구현중
        * */

        //holder.binding.videoview.player = ExoPlayer.Builder(context).build()
        var mediaItem: MediaItem = MediaItem.fromUri(item.video)
        var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

        holder.binding.videoview.player = exoPlayer
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }



}