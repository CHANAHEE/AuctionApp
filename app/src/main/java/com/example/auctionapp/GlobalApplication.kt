package com.example.auctionapp

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // 카카오 SDK 초기화
        KakaoSdk.init(this,"765a2135074ce236d9efb6feab40215a")
    }
}