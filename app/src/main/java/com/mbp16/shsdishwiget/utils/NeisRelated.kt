package com.mbp16.shsdishwiget.utils

import android.annotation.SuppressLint
import android.app.Activity
import com.google.gson.Gson
import com.opencsv.CSVReader
import java.io.InputStreamReader
import java.net.URL

fun getSchoolInfo(schoolName: String, activity: Activity): List<List<String>> {
    if (schoolName == "") {
        return listOf(listOf("", "", "", ""))
    }

    val assetManager = activity.assets
    val inputStream = assetManager.open("schoolInfo20240731.csv")
    val csvReader = CSVReader(InputStreamReader(inputStream, "EUC-KR"))
    val result: ArrayList<List<String>> = arrayListOf()
    val allContent = csvReader.readAll()
    for (content in allContent) {
        if (content[2].contains(schoolName)) {
            result.add(content.toList())
        }
        if (result.size > 4) {
            break
        }
    }

    return result.toList()
}

data class Meal(
    val MMEAL_SC_CODE: String, // 조식: 1, 중식: 2, 석식: 3
    val MMEAL_SC_NM: String, // 조식, 중식, 석식
    val MLSV_YMD: String, // 급식일자
    val DDISH_NM: String, // 급식메뉴
    val CAL_INFO: String, // 칼로리
)

@SuppressLint("DefaultLocale")
fun getNeisMeal(areaCode: String, schoolCode: String, dates: ArrayList<ArrayList<Int>>, mealType: Int): ArrayList<ArrayList<String>> {
    val fromYMD = dates[0].joinToString("", transform = { String.format("%02d", it) })
    val toYMD = dates.last().joinToString("", transform = { String.format("%02d", it) })
    val url = URL("https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json" +
            "&ATPT_OFCDC_SC_CODE=$areaCode" + "&SD_SCHUL_CODE=$schoolCode" +
            "&MLSV_FROM_YMD=$fromYMD" + "&MLSV_TO_YMD=$toYMD" + "&MMEAL_SC_CODE=$mealType")
    val connection = url.openConnection()
    var response = connection.getInputStream().bufferedReader().use { it.readText() }
    if (response.contains("INFO-000")) {
        response = response.drop(120).dropLast(3)
    } else {
        val result = ArrayList<ArrayList<String>>()
        for (i in 1..dates.size) {
            result.add(arrayListOf("No Data", "No Data", "No Data"))
        }
        return result
    }
    val parsedResponse = Gson().fromJson(response, Array<Meal>::class.java)
    val result = ArrayList<ArrayList<String>>()
    for (date in dates) {
        var meal = arrayListOf("No Data", "No Data", "No Data")
        for (i in parsedResponse) {
            if (i.MLSV_YMD == date.joinToString("", transform = { String.format("%02d", it) })) {
                meal = arrayListOf(i.MMEAL_SC_NM,
                    i.DDISH_NM.replace("<br/>", "\n").replace("\\((\\d+\\.*)+\\)".toRegex(), ""),
                    i.CAL_INFO)
                break
            }
        }
        result.add(meal)
    }
    return result
}