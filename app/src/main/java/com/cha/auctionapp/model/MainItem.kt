package com.cha.auctionapp.model

import android.net.Uri

data class CategoryItem(var cgIcon:Int, var cgName:String)

data class CategorySelectItem(var cgName:String)

data class PagerItem(var image: Uri)

data class PictureItem(var uri:Uri)





/*
*
*       홈 관련 데이터
*
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
    var id: String,
    var image: String
)




/*
*
*    커뮤니티 관련 데이터
*
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
    var idx: String,
    var title: String,
    var description: String,
    var placeinfo: String?,
    var image: String,

    var nickname: String,
    var location: String,
    var id: String,
)

data class CommentsItem(
    var description: String,
    var placeinfo: String,
    var nickname: String,
    var location: String,
    var id: String
)

data class PictureCommunityDetailItem(var path: String)



/*
*
*       경매 관련 데이터
*
* */
data class AuctionPagerItem(
    var video: Uri?,
    var image: Int?,
    var nickname: String,
    var description: String
)


/*
*
*     채팅 관련 데이터
*
* */
data class MessageItem(
    var nickname: String = "",
    var id: String = "",
    var message: String? = "",
    var time: String = "",
    var profileImage: Uri?,
    //var image: MutableList<Uri>
    var image: MutableList<Uri>,
    var imageSize: Int,
    var location: String,
    var index: Int,
    var lastOtherMessageIndex: Int
)
data class PictureMessageItem(var uri: Uri)