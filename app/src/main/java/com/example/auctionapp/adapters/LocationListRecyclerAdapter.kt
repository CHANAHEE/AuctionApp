package com.example.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.auctionapp.databinding.RecyclerLocationListBinding
import com.example.auctionapp.model.KakaoSearchItemByResionCode
import com.example.auctionapp.model.Place

class LocationListRecyclerAdapter(var context: Context, var documents: MutableList<Place>) : Adapter<LocationListRecyclerAdapter.VH>(){

    inner class VH(var binding: RecyclerLocationListBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerLocationListBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var place: Place = documents[position]
        holder.binding.tvLocationName.text = place.region_3depth_name
    }


}