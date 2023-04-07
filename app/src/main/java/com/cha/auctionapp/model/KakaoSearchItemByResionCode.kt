package com.cha.auctionapp.model

data class KakaoSearchItemByAddress(var documents: MutableList<Location>)

data class Location(var address: Address)

data class Address(var address_name: String)


