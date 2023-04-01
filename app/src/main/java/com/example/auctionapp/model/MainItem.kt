package com.example.auctionapp.model

data class MainItem(var title:String, var image:Int, var location:String, var price:String)

data class CategoryItem(var cgIcon:Int, var cgName:String)

data class CommunityPostItem(var title:String,var contents:String,var location: String)

data class PagerItem(var image:Int)