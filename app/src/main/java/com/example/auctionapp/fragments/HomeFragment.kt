package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.auctionapp.R
import com.example.auctionapp.activities.SellingEditActivity
import com.example.auctionapp.adapters.ProductAdapter
import com.example.auctionapp.databinding.FragmentHomeBinding
import com.example.auctionapp.model.Item
import java.util.ArrayList


class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    lateinit var items : MutableList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        items = mutableListOf()
        items.add(Item("Item 1",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))
        items.add(Item("Item 2",R.drawable.bg_one05,"공릉동","34000원"))

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabEdit.setOnClickListener { startActivity(Intent(context,SellingEditActivity::class.java)) }

        binding.recycler.adapter = ProductAdapter(requireContext(),items)
    }



}