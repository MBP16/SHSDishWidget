package com.mbp16.shsdishwiget

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlin.collections.ArrayList

fun GetMealData(dates: ArrayList<ArrayList<Number>>): ArrayList<ArrayList<ArrayList<String>>> {
    val ids = ArrayList<ArrayList<String>>()
    var tempYear: Number = dates[0][0]
    var tempMonth: Number = dates[0][1]
    val tempDates = ArrayList<Number>()
    fun getIds() {
        val id = GetMealId(tempYear, tempMonth, tempDates)
        ids.addAll(id)
        tempDates.clear()
    }
    for (date in dates) {
        if (tempMonth != date[1]) getIds()
        tempYear = date[0]
        tempMonth = date[1]
        tempDates.add(date[2])
        if (date == dates.last()) getIds()
    }
    val weekMeals = ArrayList<ArrayList<ArrayList<String>>>()
    for (id in ids) {
        val todayMeal = ArrayList<ArrayList<String>>()
        for (mealId in id) {
            if (mealId == "No Data") {
                todayMeal.add(arrayListOf("데이터 없음", "데이터 없음", "데이터 없음"))
            } else {
                todayMeal.add(GetData(mealId.toInt(), arrayOf("제목", "식단", "칼로리")))
            }
        }
        weekMeals.add(todayMeal)
    }
    return weekMeals
}

fun GetMealSignleWidget(year: Number, month: Number, days: Number, mealType:Int): ArrayList<String> {
    val mealId = GetMealId(year, month, arrayListOf(days))[0][mealType]
    if (mealId == "No Data") {
        return arrayListOf("데이터 없음", "데이터 없음", "데이터 없음")
    } else {
        return GetData(mealId.toInt(), arrayOf("제목", "식단", "칼로리"))
    }
}

// [["43", "43"], ["43", "43"]]
fun GetMealId(year: Number, month: Number, days: ArrayList<Number>): ArrayList<ArrayList<String>> {
    val url = "https://seoul.sen.hs.kr/77703/subMenu.do"
    val response = Jsoup.connect(url)
        .data("srhMlsvYear", year.toString())
        .data("srhMlsvMonth", month.toString())
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .post()
    val mealList = response.select("table > tbody").select("td")
    val mealIds = ArrayList<ArrayList<String>>()
    for (day in days) {
        var dayMeal: Element? = null
        for (i in mealList) {
            if (day.toString() in i.text()) {
                dayMeal = i
                break
            }
        }
        if (dayMeal == null) {
            mealIds.add(arrayListOf("No Data", "No Data"))
            continue
        }
        val meals = dayMeal.select("a")
        if (meals.size == 0) {
            mealIds.add(arrayListOf("No Data", "No Data"))
        } else if (meals.size == 1) {
            mealIds.add(arrayListOf(meals[0].attr("onclick").split("'")[1], "No Data"))
        } else {
            mealIds.add(arrayListOf(meals[0].attr("onclick").split("'")[1], meals[1].attr("onclick").split("'")[1]))
        }
    }
    return mealIds
}

fun GetData(id: Number, dataType: Array<String>): ArrayList<String> {
    val url = "https://seoul.sen.hs.kr/dggb/module/mlsv/selectMlsvDetailPopup.do?mlsvId=$id"
    val response = Jsoup.connect(url)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .post()
    val datas = ArrayList<String>()
    for (i in dataType) {
        val data = response.select("th:contains($i)").first()?.nextElementSibling()?.text()
        if (data != null) {
            datas.add(data)
        }
    }
    return datas
}
