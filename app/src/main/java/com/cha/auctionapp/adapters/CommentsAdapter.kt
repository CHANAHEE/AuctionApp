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
import com.cha.auctionapp.R
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
        Log.i("iiiiddd",item.placeinfo ?: "dddafasf")
        if(item.placeinfo != ""){
            holder.binding.relativeLocation.visibility = View.VISIBLE
            holder.binding.tvLocationName.text = item.placeinfo
        }

        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")

        Log.i("iiii",item.id)
        userRef.document(item.id).get().addOnSuccessListener {
            holder.binding.tvOtherId.text = it.get("nickname").toString()
            return@addOnSuccessListener
        }

        loadProfileFromFirestore(holder,item)
    }


    private fun loadProfileFromFirestore(holder: VH, item: CommentsItem){
        val firebaseStorage = FirebaseStorage.getInstance()
        val rootRef = firebaseStorage.reference

        val imgRef = rootRef.child( "IMG_" + item.id + ".jpg")
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