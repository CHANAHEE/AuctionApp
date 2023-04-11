package com.cha.auctionapp

import android.net.Uri
import com.cha.auctionapp.model.UserAccount

class G{

    companion object{
        // 프로필 이미지
        var profile: Uri = Uri.parse("")
        var nickName: String = ""
        var location: String = ""
        var userAccount: UserAccount = UserAccount("","")
        var imgPath: String = ""
    }
}