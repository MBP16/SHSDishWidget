package com.mbp16.shsdishwiget

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlin.collections.ArrayList

fun GetMealData(dates: ArrayList<ArrayList<Number>>): ArrayList<ArrayList<ArrayList<String>>> {
    val ids = ArrayList<ArrayList<String>>()
    for (date in dates) {
        val id = GetMealId(date[0], date[1], date[2])
        ids.add(id)
    }
    val weekMeals = ArrayList<ArrayList<ArrayList<String>>>()
    for (id in ids) {
        val todayMeal = ArrayList<ArrayList<String>>()
        for (mealId in id) {
            if (mealId == "No Data") {
                todayMeal.add(arrayListOf("No Data", "No Data", "No Data"))
            } else {
                todayMeal.add(GetData(mealId.toInt(), arrayOf("제목", "식단", "칼로리")))
            }
        }
        weekMeals.add(todayMeal)
    }
    return weekMeals
}

fun GetMealId(year: Number, month: Number, day: Number): ArrayList<String> {
    val url = "https://seoul.sen.hs.kr/77703/subMenu.do"
    val response = Jsoup.connect(url)
        .data("srhMlsvYear", year.toString())
        .data("srhMlsvMonth", month.toString())
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .post()
    val mealList = response.select("table > tbody").select("td")
    var meal: Element? = null
    for (i in mealList) {
        if (day.toString() in i.text()) {
            meal = i
            break
        }
    }
    if (meal == null) {
        return arrayListOf("No Data", "No Data")
    }
    val meals = meal.select("a")
    if (meals.size == 0) {
        return arrayListOf("No Data", "No Data")
    } else if (meals.size == 1) {
        return arrayListOf(meals[0].attr("onclick").split("'")[1], "No Data")
    } else {
        return arrayListOf(meals[0].attr("onclick").split("'")[1], meals[1].attr("onclick").split("'")[1])
    }
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
