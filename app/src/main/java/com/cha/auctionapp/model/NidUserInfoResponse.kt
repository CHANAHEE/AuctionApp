package com.cha.auctionapp.model

data class NidUserInfoResponse(var resultcode: String, var message: String, var response: UserInfo)

data class UserInfo(var email: String, var id: String)
