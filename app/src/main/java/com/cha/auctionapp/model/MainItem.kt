package com.cha.auctionapp.model

import android.net.Uri

data class CategoryItem(var cgIcon:Int, var cgName:String)

data class CategorySelectItem(var cgName:String)

data class PagerItem(var image: Uri)

data class PictureItem(var uri:Uri)

data class AuctionPagerItem(var video: Uri, var image: Int, var id: String, var description: String)

data class MessageItem(var image: Int, var id: String, var message: String, var time: String)

/*
*       홈 관련 데이터
* */
data class MainItem(
    var idx: Int,
    var title:String,
    var image:String,
    var location:String,
    var price:String
)

data class HomeDetailItem(
    var title: String,
    var category: String,
    var price: String,
    var description: String,
    var tradingplace: String,
    var nickname: String,
    var location: String,
    var profile: String,
    var image: String
)

/*
*       커뮤니티 관련 데이터
* */
data class CommunityPostItem(
    var idx: Int,
    var image:String,
    var title:String,
    var description: String,
    var location: String,
    var fav:Int,
    var comments:Int
)

data class CommunityDetailItem(
    var title: String,
    var description: String,
    var placeinfo: String,
    var image: String,

    var nickname: String,
    var location: String,
    var profile: String,
)

data class CommentsItem(
    var image: Int,
    var id: String,
    var town: String,
    var description: String,
    var location: String?
)

data class PictureCommunityDetailItem(var path: String)