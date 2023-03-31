package com.example.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.activities.SearchItemByCategoryActivity
import com.example.auctionapp.databinding.RecyclerCategoryItemBinding
import com.example.auctionapp.model.CategoryItem

class CategoryAdapter(var context: Context, var categoryItems:MutableList<CategoryItem>) : Adapter<CategoryAdapter.VH>() {


    inner class VH(var binding:RecyclerCategoryItemBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCategoryItemBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount(): Int = categoryItems.size
    override fun onBindViewHolder(holder: VH, position: Int) {

        var item:CategoryItem = categoryItems[position]
        Glide.with(context).load(item.cgIcon).into(holder.binding.ivCategory)
        holder.binding.tvCategory.text = item.cgName



        /*
        *
        *       아이템 클릭 리스너 처리
        *
        * */
        holder.binding.root.setOnClickListener {
            context.startActivity(Intent(context,SearchItemByCategoryActivity::class.java))
        }
    }
}