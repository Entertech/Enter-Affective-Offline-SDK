package cn.entertech.biomoduledemo.utils

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

fun getCurrentTime():String{
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
    return simpleDateFormat.format(Date())
}