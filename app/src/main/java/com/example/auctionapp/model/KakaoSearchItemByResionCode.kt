package com.example.auctionapp.model

data class KakaoSearchItemByResionCode(var meta: PlaceMeta, var documents: MutableList<Place>)

data class PlaceMeta(var total_count: Int)

data class Place(var region_3depth_name: String)

data class KakaoSearchItemByAddress(var documents: MutableList<Location>)

data class Location(var address: Address)

data class Address(var address_name: String)


