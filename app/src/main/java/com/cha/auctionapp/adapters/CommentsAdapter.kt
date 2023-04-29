package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.SelectPositionActivity
import com.cha.auctionapp.databinding.RecyclerCommentsItemBinding
import com.cha.auctionapp.model.CommentsItem
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CommentsAdapter(var context: Context, var items: MutableList<CommentsItem>) : Adapter<CommentsAdapter.VH>(){

    inner class VH(var binding: RecyclerCommentsItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommentsItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        var item = items[position]
        holder.binding.tvCommentsDetail.text = item.description
        holder.binding.tvOtherId.text = item.nickname
        holder.binding.tvOtherTownName.text = item.location
        if(item.placeinfo.isNotBlank()){
            holder.binding.relativeLocation.visibility = View.VISIBLE
            holder.binding.tvLocationName.text = item.placeinfo
            holder.binding.relativeLocation.setOnClickListener { clickLocation(item) }
        }

        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")

        userRef.document(item.id).get().addOnSuccessListener {
            holder.binding.tvOtherId.text = it.get("nickname").toString()
            Glide.with(context).load(it.get("profileImage")).error(R.drawable.default_profile).into(holder.binding.civOtherProfile)
            return@addOnSuccessListener
        }
    }

    private fun clickLocation(item: CommentsItem){
        context.startActivity(
            Intent(context, SelectPositionActivity::class.java)
            .putExtra("showLocation","showLocation")
            .putExtra("latitude",item.latitude)
            .putExtra("longitude",item.longitude)
            .putExtra("title",item.placeinfo))
    }
}