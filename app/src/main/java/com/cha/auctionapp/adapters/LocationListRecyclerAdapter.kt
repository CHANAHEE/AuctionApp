package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.LoginActivity
import com.cha.auctionapp.activities.MainActivity
import com.cha.auctionapp.activities.MyProfileEditActivity
import com.cha.auctionapp.activities.SetUpMyPlaceListActivity
import com.cha.auctionapp.databinding.FragmentSignUpSetUpPlaceBinding
import com.cha.auctionapp.databinding.RecyclerLocationListBinding
import com.cha.auctionapp.model.Location
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date


class LocationListRecyclerAdapter() : Adapter<LocationListRecyclerAdapter.VH>(){

    lateinit var context: Context
    lateinit var documents: MutableList<Location>
    lateinit var bindingFrag: FragmentSignUpSetUpPlaceBinding
    constructor(context: Context,documents: MutableList<Location>,bindingFrag: FragmentSignUpSetUpPlaceBinding) : this(){
        this.context = context
        this.documents = documents
        this.bindingFrag = bindingFrag
    }

    constructor(context: Context,documents: MutableList<Location>) : this() {
        this.context = context
        this.documents = documents
    }

    inner class VH(var binding: RecyclerLocationListBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerLocationListBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        var address: String = documents[position].address.address_name
        holder.binding.tvLocationName.text = "${address} "



        if(context is LoginActivity) {
            holder.itemView.setOnClickListener {
                bindingFrag.tvLocationSetUpPlace.text = holder.binding.tvLocationName.text
            }
        }
        else{

            holder.binding.root.setOnClickListener {

                var activity = context as SetUpMyPlaceListActivity
                G.location = it.findViewById<TextView>(R.id.tv_location_name).text.toString()
                val list = G.location.split(" ")
                G.location = list[list.lastIndex - 1]
                when(activity.intent.getStringExtra("Community")){
                    "Community"->{
                        saveUserInfo()
                        context.startActivity(Intent(context,MainActivity::class.java).putExtra("Community","Community"))
                    }
                    else->{
                        saveUserInfo()
                        context.startActivity(Intent(context,MainActivity::class.java).putExtra("Home","Home"))
                    }
                }
                activity.setResult(android.app.Activity.RESULT_OK)
                activity.finish()

            }
        }
    }



    private fun saveUserInfo(){
        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")

        var user: MutableMap<String,Any> = mutableMapOf<String,Any>()
        user.put("id",G.userAccount.id)
        user.put("email",G.userAccount?.email!!)
        user.put("location",G.location)
        user.put("nickname",G.nickName)
        user.put("profile",G.profile.toString())

        userRef.document(G.userAccount?.id!!).update("location",G.location)
    }
}