package com.cha.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.RecyclerAuctionCommentsItemBinding
import com.cha.auctionapp.model.CommentsItem
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class AuctionCommentsAdapter(var context: Context, var items: MutableList<CommentsItem>) : Adapter<AuctionCommentsAdapter.VH>(){

    inner class VH(var binding: RecyclerAuctionCommentsItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerAuctionCommentsItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        var item = items[position]
        holder.binding.tvCommentsDetail.text = item.description
        holder.binding.tvOtherId.text = item.nickname
        holder.binding.tvOtherTownName.text = item.location

        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")

        userRef.document(item.id).get().addOnSuccessListener {
            holder.binding.tvOtherId.text = it.get("nickname").toString()
            Glide.with(context).load(it.get("profileImage")).error(R.drawable.default_profile).into(holder.binding.civOtherProfile)
            return@addOnSuccessListener
        }
    }
}