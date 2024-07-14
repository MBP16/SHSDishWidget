package com.mbp16.shsdishwiget.utils

import android.content.Context
import androidx.room.Room
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun getMeals(dates: ArrayList<ArrayList<Int>>, context: Context): ArrayList<ArrayList<ArrayList<String>>> {
    val requestMeals = ArrayList<ArrayList<Int>>()
    for (i in dates) {
        requestMeals.add(arrayListOf(i[0], i[1], i[2], 0))
        requestMeals.add(arrayListOf(i[0], i[1], i[2], 1))
    }
    val totalMeals = getMeal(context, requestMeals)
    val weekMeals = ArrayList<ArrayList<ArrayList<String>>>()
    for (i in 0 until dates.size) {
        weekMeals.add(arrayListOf(totalMeals[i*2], totalMeals[i*2+1]))
    }
    return weekMeals
}

fun getMealSignleWidget(year: Int, month: Int, days: Int, mealType:Int, context: Context): ArrayList<String> {
    return getMeal(context, arrayListOf(arrayListOf(year, month, days, mealType)))[0]
}

// meals: [[year, month, day, mealType], ...]
fun getMeal(context: Context, meals: ArrayList<ArrayList<Int>>): ArrayList<ArrayList<String>> {
    val db = Room.databaseBuilder(context, AppDatabase::class.java, "mealData").allowMainThreadQueries().enableMultiInstanceInvalidation().build()
    val mealDataDao = db.mealDataDao()
    val resultData = ArrayList<ArrayList<String>>()
    val neededDates = ArrayList<String>()
    for (meal in meals) {
        val date = "${meal[0]}-${meal[1]}-${meal[2]}-${meal[3]}"
        neededDates.add(date)
    }
    val mealDatas = mealDataDao.getByDateTypes(neededDates)
    val neededMeals = ArrayList<ArrayList<Int>>()
    if (mealDatas.isEmpty()) {
        for (meal in meals) {
            neededMeals.add(meal)
            resultData.add(arrayListOf("null", "null", "null"))
        }
    } else {
        for (meal in meals) {
            for (data in mealDatas) {
                if (data.dateType == "${meal[0]}-${meal[1]}-${meal[2]}-${meal[3]}") {
                    resultData.add(arrayListOf(data.title, data.meal, data.calorie))
                    break
                } else if (data == mealDatas.last()) {
                    neededMeals.add(meal)
                    resultData.add(arrayListOf("null", "null", "null"))
                }
            }
        }
    }
    if (neededMeals.size > 0) {
        val newMeals = getMealFromServer(neededMeals)
        for (i in 0 until neededMeals.size) {
            val meal = neededMeals[i]
            val newMeal = newMeals[i]
            mealDataDao.insert(MealData("${meal[0]}-${meal[1]}-${meal[2]}-${meal[3]}", newMeal[0], newMeal[1], newMeal[2]))
        }
        var tempIndex = 0
        for (i in 0 until resultData.size) {
            if (resultData[i][0] == "null") {
                resultData[i] = newMeals[tempIndex]
                tempIndex++
            }
        }
    }
    db.close()
    return resultData
}

fun getMealFromServer(meals: ArrayList<ArrayList<Int>>): ArrayList<ArrayList<String>> {
    val ids = ArrayList<String>()
    var tempYear: Number = meals[0][0]
    var tempMonth: Number = meals[0][1]
    val tempDateTypes = ArrayList<ArrayList<Int>>()
    fun getIds() {
        val id = getMealId(tempYear, tempMonth, tempDateTypes)
        ids.addAll(id)
        tempDateTypes.clear()
    }
    for (date in meals) {
        if (tempMonth != date[1]) getIds()
        tempYear = date[0]
        tempMonth = date[1]
        if (date[3] == 1 && tempDateTypes.isNotEmpty() && tempDateTypes.last()[0] == date[2]) {
            tempDateTypes.removeAt(tempDateTypes.size-1)
            tempDateTypes.add(arrayListOf(date[2], 2))
        } else {
            tempDateTypes.add(arrayListOf(date[2], date[3]))
        }
        if (date == meals.last()) getIds()
    }
    println(ids)
    val resultMeals = ArrayList<ArrayList<String>>()
    for (id in ids) {
        if (id == "No Data") { resultMeals.add(arrayListOf("데이터 없음", "데이터 없음", "데이터 없음")) }
        else { resultMeals.add(getMealData(id.toInt())) }
    }
    return resultMeals
}

fun getMealId(year: Number, month: Number, dayTypes: ArrayList<ArrayList<Int>>): ArrayList<String> {
    val url = "https://seoul.sen.hs.kr/77703/subMenu.do"
    val response = Jsoup.connect(url)
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
                0, 1 -> mealIds.add("No Data")
                else -> mealIds.addAll(arrayListOf("No Data", "No Data"))
            }
            continue
        }
        val meals = dayMeal.select("a")
        if (day[1] == 0 || day[1] == 1) {
            if (meals.size >= (day[1]+1)) { mealIds.add(meals[day[1]].attr("onclick").split("'")[1]) }
            else { mealIds.add("No Data") }
        } else {
            when (meals.size) {
                0 -> mealIds.addAll(arrayListOf("No Data", "No Data"))
                1 -> mealIds.addAll(arrayListOf(meals[0].attr("onclick").split("'")[1], "No Data"))
                else -> mealIds.addAll(arrayListOf(meals[0].attr("onclick").split("'")[1], meals[1].attr("onclick").split("'")[1]))
            }
        }
    }
    return mealIds
}

fun getMealData(id: Number): ArrayList<String> {
    val url = "https://seoul.sen.hs.kr/dggb/module/mlsv/selectMlsvDetailPopup.do?mlsvId=$id"
    val response = Jsoup.connect(url)
        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
        .post()
    val datas = ArrayList<String>()
    for (i in arrayOf("제목", "식단", "칼로리")) {
        val data = response.select("th:contains($i)").first()?.nextElementSibling()?.text()
        if (data != null) {
            datas.add(data)
        }
    }
    return datas
}
