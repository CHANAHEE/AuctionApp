package com.cha.auctionapp.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

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

data class CategorySearchItem(
    var idx: Int,
    var title: String,
    var image: String,
    var location: String,
    var price: String
)
data class MyPostListItem(
    var idx: Int,
    var title: String,
    var image: String,
    var location: String,
    var price: String
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
    var place_info: String?,
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
    var idx: Int,
    var video: String,
    var description: String,
    var id: String
)

data class AuctionDetailItem(
    var title: String,
    var category: String,
    var price: String,
    var description: String,
    var tradingplace: String,
    var location: String,
    var video: String,
    var id: String
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
    var image: MutableList<Uri>,
    var imageSize: Int,
    var location: String,
    var messageIndex: Int,
    var lastOtherMessageIndex: Int,
    var otherProfileImage: String?,
    var otherID: String,
    var otherNickname: String
)
data class ChatListItem(
    var nickname: String,
    var profileImage: String?,
    var lastMessage: String,
    var time: String,
    var OtherID: String
)

/*
*
*       관심 목록 데이터
*
* */
@Entity
data class MyFavListItem(
    @PrimaryKey var idx: String,
    @ColumnInfo(name = "index") var indexProduct: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "location") var location: String,
    @ColumnInfo(name = "price") var price: String,
    @ColumnInfo(name = "image") var image: String
)

@Database(entities = [MyFavListItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myFavListItemDAO(): MyFavListItemDAO
}

@Dao
interface MyFavListItemDAO{
    @Query("SELECT * FROM myFavListItem")
    fun getAll() : List<MyFavListItem>

    @Insert
    fun insert(myFavProduct: MyFavListItem)

    @Delete
    fun delete(myFavProduct: MyFavListItem)
}


