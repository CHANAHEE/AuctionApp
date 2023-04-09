package com.cha.auctionapp.network

import android.net.Uri
import com.cha.auctionapp.model.KakaoSearchItemByAddress
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


//    @Multipart
//    @POST("Server/insertDB.php")
//    fun postDataToServer(@PartMap dataPart: HashMap<String,String> , @Part profile: MultipartBody.Part) : Call<String>
    @Multipart
    @POST("Server/insertDB.php")
    fun postDataToServer(@PartMap dataPart: HashMap<String,String> , @Part image: MutableList<MultipartBody.Part>) : Call<String>

}