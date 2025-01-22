package com.englishto.urdu.translator.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Utils {

    fun isNetConnected(mContext: Context): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork
        return if (nw == null) {
//            Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_LONG).show()
            false
        } else {
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            if (actNw!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                true
            } else if (actNw!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                true
            } else {
//                Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_LONG).show()
                false
            }
        }
    }

}