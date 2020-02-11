package com.testgithub.common

import java.text.SimpleDateFormat
import java.util.*

object TextUtils {

    fun convertServerDate(sourceDate: String): String {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            .parse(sourceDate)
        return date?.let {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(it)
        } ?: ""
    }
}