package com.example.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.auctionapp.activities.LoginActivity
import com.example.auctionapp.databinding.FragmentSignUpSetUpPlaceBinding
import com.example.auctionapp.databinding.RecyclerLocationListBinding
import com.example.auctionapp.model.Location
import com.example.auctionapp.model.KakaoSearchItemByResionCode
import com.example.auctionapp.model.Place

class LocationListRecyclerAdapter(var context: Context, var documents: MutableList<Location>,var bindingFrag: FragmentSignUpSetUpPlaceBinding) : Adapter<LocationListRecyclerAdapter.VH>(){

    inner class VH(var binding: RecyclerLocationListBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerLocationListBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var address: String = documents[position].address.address_name
        holder.binding.tvLocationName.text = "${address} "

        holder.itemView.setOnClickListener { bindingFrag.tvLocationSetUpPlace.text = holder.binding.tvLocationName.text}
    }


}