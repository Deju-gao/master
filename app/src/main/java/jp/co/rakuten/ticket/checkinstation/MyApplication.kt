package jp.co.rakuten.ticket.checkinstation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    private var cookie: String = ""

    fun setCookie(content:String) {
        cookie = content
    }

    fun getCookie(): String {
        return cookie
    }

}