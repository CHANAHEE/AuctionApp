package com.cha.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.SellingEditActivity
import com.cha.auctionapp.adapters.ProductAdapter
import com.cha.auctionapp.databinding.FragmentHomeBinding
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    lateinit var mainItem: MutableList<MainItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainItem = mutableListOf()
        binding.recycler.adapter = ProductAdapter(requireContext(),mainItem)
        binding.fabEdit.setOnClickListener { startActivity(Intent(context,SellingEditActivity::class.java)) }
        binding.refreshLayout.setOnRefreshListener { clickRefresh() }

        binding.refreshLayout.visibility = View.GONE
        binding.shimmerRecyclerView.showShimmerAdapter()
    }

    fun setData(items: MutableList<MainItem>){
        binding.recycler.adapter = ProductAdapter(requireContext(),items)

    }
    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<MainItem>> = retrofitService.getDataFromServerForHomeFragment(G.location)
        Log.i("test19",G.location)
        
        try {
            call.enqueue(object : Callback<MutableList<MainItem>>{
                override fun onResponse(
                    call: Call<MutableList<MainItem>>,
                    response: Response<MutableList<MainItem>>
                ) {
                    mainItem = response.body()!!
                    if(activity == null || !isAdded) return
                    mainItem.sortByDescending {
                        it.idx
                    }
                    binding.recycler.adapter = ProductAdapter(requireContext(),mainItem)
                    binding.refreshLayout.isRefreshing = false

                    binding.refreshLayout.visibility = View.VISIBLE
                    binding.shimmerRecyclerView.visibility = View.GONE
                }

                override fun onFailure(call: Call<MutableList<MainItem>>, t: Throwable) {
                    Log.i("test01","${t.message}")
                }
            })
        }catch (e: Exception){
            Toast.makeText(context, "HomeFragment 네트워크 작업 실패", Toast.LENGTH_SHORT).show()
        }
    }


    /*
    *
    *       리프레시
    *
    * */
    private fun clickRefresh() = loadData()
}