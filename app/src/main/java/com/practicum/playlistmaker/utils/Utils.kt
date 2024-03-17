package com.practicum.playlistmaker.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

const val LOG_TAG = "playlistmaker"

//Константы для Koin
const val QUALIFIER_IMAGE_DIRECTORY = "imageDirectory"

fun getTrackCountNoun(count: Int): String {
    val lastDigit = count % 100
    if (lastDigit in 11..14)
        return "треков"
    return when (lastDigit % 10) {
        1 -> "трек"
        2, 3, 4 -> "трека"
        else -> "треков"
    }
}

fun getMinuteCountNoun(count: Long): String {
    val lastDigit = (count % 100).toInt()
    if (lastDigit in 11..14)
        return "минут"
    return when (lastDigit % 10) {
        1 -> "минута"
        2, 3, 4 -> "минуты"
        else -> "минут"
    }
}

fun networkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
        }
    }
    return false
}