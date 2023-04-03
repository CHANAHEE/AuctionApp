package com.example.auctionapp.model

import android.net.Uri

data class MainItem(var title:String, var image:Int, var location:String, var price:String)

data class CategoryItem(var cgIcon:Int, var cgName:String)

data class CategorySelectItem(var cgName:String)

data class CommunityPostItem(var image:Int , var title:String,var contents:String,var location: String,var fav:Int,var comments:Int)

data class PagerItem(var image:Int)

data class PictureItem(var uri:Uri)

data class CommentsItem(var image: Int, var id: String, var town: String, var description: String, var location: String?)