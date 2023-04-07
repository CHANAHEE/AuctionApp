package com.cha.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cha.auctionapp.databinding.FragmentSignUpSetUpPlaceBinding
import com.cha.auctionapp.databinding.RecyclerLocationListBinding
import com.cha.auctionapp.model.Location


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