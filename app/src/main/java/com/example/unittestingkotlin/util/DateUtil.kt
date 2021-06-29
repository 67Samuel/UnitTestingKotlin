package com.example.unittestingkotlin.util

import com.example.unittestingkotlin.util.Constants.Companion.DATE_FORMAT
import com.example.unittestingkotlin.util.Constants.Companion.GET_MONTH_ERROR
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    companion object {

        private val TAG: String = "DateUtilDebug"

        val months = arrayOf("Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec")

        val monthNumbers = arrayOf(
            "01",
            "02",
            "03",
            "04",
            "05",
            "06",
            "07",
            "08",
            "09",
            "10",
            "11",
            "12"
        )

        fun getCurrentTimestamp(): String {
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
            try {
                return dateFormat.format(Date())
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("Could not format date into $DATE_FORMAT")
            }
        }

        fun getMonthFromNumber(monthNumber: String): String {
            return when (monthNumber) {
                "01" -> months[0]
                "02" -> months[1]
                "03" -> months[2]
                "04" -> months[3]
                "05" -> months[4]
                "06" -> months[5]
                "07" -> months[6]
                "08" -> months[7]
                "09" -> months[8]
                "10" -> months[9]
                "11" -> months[10]
                "12" -> months[11]
                else -> "$GET_MONTH_ERROR$monthNumber"
            }
        }
    }
}