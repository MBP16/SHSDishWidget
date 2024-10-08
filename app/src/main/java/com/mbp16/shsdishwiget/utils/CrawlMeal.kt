package com.mbp16.shsdishwiget.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

//Type 1: 서울시 교육청
fun type1GetMeal(mealPagelink: String, mealIdLink: String, year: Number, month: Number, dayTypes: ArrayList<ArrayList<Int>>): ArrayList<ArrayList<String>> {
    var response = Jsoup.connect(mealPagelink)
        .data("srhMlsvYear", year.toString())
        .data("srhMlsvMonth", month.toString())
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .post()
    val mealList = response.select("table > tbody").select("td")
    val mealIds = ArrayList<String>()
    for (day in dayTypes) {
        var dayMeal: Element? = null
        for (i in mealList) {
            if (day[0].toString() in i.text()) {
                dayMeal = i
                break
            }
        }
        if (dayMeal == null) {
            when (day[1]) {
                2, 3 -> mealIds.add("No Data")
                else -> mealIds.addAll(listOf("No Data", "No Data"))
            }
            continue
        }
        val meals = dayMeal.select("a")
        if (day[1] == 2 || day[1] == 3) {
            if (meals.size >= (day[1]-1)) { mealIds.add(meals[day[1]-2].attr("onclick").split("'")[1]) }
            else { mealIds.add("No Data") }
        } else {
            when (meals.size) {
                0 -> mealIds.addAll(listOf("No Data", "No Data"))
                1 -> mealIds.addAll(listOf(meals[0].attr("onclick").split("'")[1], "No Data"))
                else -> mealIds.addAll(listOf(meals[0].attr("onclick").split("'")[1], meals[1].attr("onclick").split("'")[1]))
            }
        }
    }
    println(mealIds)
    fun type1GetMealData(link: String, id: Number): ArrayList<String> {
        response = Jsoup.connect(link)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
            .data("mlsvId", id.toString())
            .post()
        val datas = ArrayList<String>()
        for (i in arrayOf("제목", "식단", "칼로리")) {
            var data = response.select("th:contains($i)").first()?.nextElementSibling()?.text()
            if (i == "식단") {
                data = data
                    ?.replace(" ", "\n")
                    ?.replace(",", "\n")
                    ?.replace("\\((\\d+\\.*)+\\)".toRegex(), "\n")
                    ?.replace("\n\n", "\n")
            }
            if (data != null) {
                datas.add(data)
            }
        }
        return datas
    }
    val resultMeals = ArrayList<ArrayList<String>>()
    for (id in mealIds) {
        if (id == "No Data") {
            resultMeals.add(arrayListOf("데이터 없음", "데이터 없음", "데이터 없음"))
        } else {
            resultMeals.add(type1GetMealData(mealIdLink, id.toInt()))
        }
    }
    return resultMeals
}

//Type 2: 경기도 교육청
fun type2GetMeal(mealPagelink: String, mealIdLink: String, year: Number, month: Number, dayTypes: ArrayList<ArrayList<Int>>): ArrayList<ArrayList<String>> {
    return arrayListOf(arrayListOf("데이터 없음", "데이터 없음", "데이터 없음"))
}