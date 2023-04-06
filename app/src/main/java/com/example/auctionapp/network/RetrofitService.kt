package com.example.auctionapp.network

import com.example.auctionapp.model.KakaoSearchItemByAddress
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {

    @Headers("Authorization: KakaoAK aff24ec839bdd82ea5acc8e4783a3e83")
    @GET("/v2/local/search/address")
    fun getSearchPlaceByAddress(@Query("query") query: String) : Call<KakaoSearchItemByAddress>
}