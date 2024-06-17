package com.mbp16.shsdishwiget

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun GetMealId(year: Number, month: Number, day: Number, mealType: Int): String {
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
        return "No Data"
    }
    return meal.select("a")[mealType].attr("onclick").split("'")[1]
}

fun GetMealData(id: Number, dataType: String): String? {
    val url = "https://seoul.sen.hs.kr/dggb/module/mlsv/selectMlsvDetailPopup.do?mlsvId=$id"
    val response = Jsoup.connect(url)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .post()
    val meal = response.select("th:contains($dataType)").first()?.nextElementSibling()?.text()
    return meal
}
