package com.cha.auctionapp.network

import com.cha.auctionapp.model.CommentsItem
import com.cha.auctionapp.model.CommunityDetailItem
import com.cha.auctionapp.model.CommunityPostItem
import com.cha.auctionapp.model.HomeDetailItem
import com.cha.auctionapp.model.KakaoSearchItemByAddress
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.model.NidUserInfoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface RetrofitService {

    @Headers("Authorization: KakaoAK aff24ec839bdd82ea5acc8e4783a3e83")
    @GET("/v2/local/search/address")
    fun getSearchPlaceByAddress(@Query("query") query: String) : Call<KakaoSearchItemByAddress>

    @GET("v1/nid/me")
    fun getNaverUserInfo(@Header("Authorization") authorization: String) : Call<NidUserInfoResponse>


    // 서버로 데이터 전송

    @Multipart
    @POST("Server/insertDBForHomeFragment.php")
    fun postDataToServerForHomeFragment(@PartMap dataPart: HashMap<String,String>, @Part image: MutableList<MultipartBody.Part>) : Call<String>

    @Multipart
    @POST("Server/insertDBForCommunityFragment.php")
    fun postDataToServerForCommunityFragment(@PartMap dataPart: HashMap<String,String>, @Part image: MutableList<MultipartBody.Part>) : Call<String>

    @Multipart
    @POST("Server/insertDBForCommunityFragmentComments.php")
    fun postDataToServerForCommunityDetailComments(@PartMap dataPart: HashMap<String,String>) : Call<String>

    // 서버에서 데이터 받아오기

    @GET("Server/loadDBForHomeFragment.php")
    fun getDataFromServerForHomeFragment(@Query("location") query: String) : Call<MutableList<MainItem>>

    @GET("Server/loadDBForHomeDetail.php")
    fun getDataFromServerForHomeDetail(@Query("index") query: String) : Call<MutableList<HomeDetailItem>>

    @GET("Server/loadDBForCommunityFragment.php")
    fun getDataFromServerForCommunityFragment(@Query("location") query: String) : Call<MutableList<CommunityPostItem>>

    @GET("Server/loadDBForCommunityDetail.php")
    fun getDataFromServerForCommunityDetail(@Query("index") query: String) : Call<MutableList<CommunityDetailItem>>

    @GET("Server/loadDBForCommunityDetailComments.php")
    fun getDataFromServerForCommunityDetailComments(@Query("index") query: String) : Call<MutableList<CommentsItem>>
}