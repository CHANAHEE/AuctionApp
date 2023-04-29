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
    var image: String,
    var latitude: String,
    var longitude: String
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
    var latitude: String,
    var longitude: String
)

data class CommentsItem(
    var description: String,
    var placeinfo: String,
    var nickname: String,
    var location: String,
    var id: String,
    var latitude: String,
    var longitude: String
)

data class PictureCommunityDetailItem(var path: String)
data class MyCommunityPostList(
    var idx: String,
    var title: String,
    var location: String,
    var description: String,
    var image: String
)

/*
*
*       경매 관련 데이터
*
* */
data class AuctionPagerItem(
    var idx: Int,
    var video: String,
    var description: String,
    var id: String,
    var now: String
)

data class AuctionDetailItem(
    var title: String,
    var category: String,
    var price: String,
    var description: String,
    var tradingplace: String,
    var location: String,
    var video: String,
    var id: String,
    var latitude: String,
    var longitude: String
)

data class MyAuctionPostList(
    var idx: String,
    var title:String,
    var location: String,
    var description: String,
    var time: String
)



/*
*
*     채팅 관련 데이터
*
* */
data class MessageItem(
    var productIndex: String,
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
    var otherNickname: String,
    var chatRoomInfo: ChatRoomInfo,
    var latitude: String,
    var longitude: String
)
data class ChatListItem(
    var productIndex: String,
    var nickname: String,
    var profileImage: String?,
    var lastMessage: String,
    var time: String,
    var OtherID: String,
    var chatRoomInfo: ChatRoomInfo
)

data class ChatRoomInfo(
    var titleProductInfo: String,
    var locationProductInfo: String,
    var priceProductInfo: String,
    var imageProductInfo: String
)


/*
*
*       찜한 목록 데이터
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

@Entity
data class MyCommunityFavListItem(
    @PrimaryKey var idx: String,
    @ColumnInfo(name = "index") var indexProduct: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "location") var location: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "image") var image: String
)

@Entity
data class MyAuctionFavListItem(
    @PrimaryKey var idx: String,
    @ColumnInfo(name = "index") var indexProduct: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "location") var location: String,
    @ColumnInfo(name = "price") var price: String
)


@Database(entities = [MyFavListItem::class,MyCommunityFavListItem::class,MyAuctionFavListItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myFavListItemDAO(): MyFavListItemDAO
    abstract fun MyCommunityFavListItemDAO(): MyCommunityFavListItemDAO
    abstract fun MyAuctionFavListItemDAO(): MyAuctionFavListItemDAO
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

@Dao
interface MyCommunityFavListItemDAO{
    @Query("SELECT * FROM myCommunityFavListItem")
    fun getAll() : List<MyCommunityFavListItem>

    @Insert
    fun insert(myCommunityFavProduct: MyCommunityFavListItem)

    @Delete
    fun delete(myCommunityFavProduct: MyCommunityFavListItem)
}

@Dao
interface MyAuctionFavListItemDAO{
    @Query("SELECT * FROM myauctionfavlistitem")
    fun getAll() : List<MyAuctionFavListItem>

    @Insert
    fun insert(myAuctionFavProduct: MyAuctionFavListItem)

    @Delete
    fun delete(myAuctionFavProduct: MyAuctionFavListItem)
}



