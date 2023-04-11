package com.cha.auctionapp.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.RecyclerCommentsItemBinding
import com.cha.auctionapp.model.CommentsItem
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage

class CommentsAdapter(var context: Context, var items: MutableList<CommentsItem>) : Adapter<CommentsAdapter.VH>(){

    inner class VH(var binding: RecyclerCommentsItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommentsItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        var item = items[position]
        holder.binding.tvCommentsDetail.text = item.description
        holder.binding.tvOtherId.text = item.id
        holder.binding.tvOtherTownName.text = item.town
        if(item.location != null){
            holder.binding.relativeLocation.visibility = View.VISIBLE
            holder.binding.tvLocationName.text = item.location
        }

    }


    private fun loadProfileFromFirestore(holder: VH){
        val firebaseStorage = FirebaseStorage.getInstance()
        val rootRef = firebaseStorage.reference

        val imgRef = rootRef.child( "IMG_" + G.userAccount.id + ".jpg")
        if (imgRef != null) {
            imgRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri?> {
                override fun onSuccess(p0: Uri?) {
                    Glide.with(context).load(p0).error(R.drawable.default_profile).into(holder.binding.civOtherProfile)
                }
            }).addOnFailureListener {
                Log.i("test12344",it.toString())
            }
        }
    }
}