package jp.co.rakuten.ticket.checkinstation.util

import android.content.Context
import android.net.*
import android.os.Build
import jp.co.rakuten.ticket.checkinstation.R

class NetworkConnectUtil {

    companion object {
        fun isConnected(context: Context) {
            if (!isNetworkConnect(context)) {
                BcpUtil.getInstance()
                    .showAlertDialog(context, context.getString(R.string.error_network))
            }
        }

        @Suppress("DEPRECATION")
        fun isNetworkConnect(context: Context): Boolean {
            var result = false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager?.run {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                        result = when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                connectivityManager?.run {
                    connectivityManager.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            result = true
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            result = true
                        }
                    }
                }
            }
            return result
        }
    }

}