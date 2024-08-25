package com.mbp16.shsdishwiget.utils

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.dataStore
import androidx.room.Room
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore.Companion.mealDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.Calendar

fun getMeals(dates: ArrayList<ArrayList<Int>>, context: Context): ArrayList<ArrayList<ArrayList<String>>> {
    val requestMeals = ArrayList<ArrayList<Int>>()
    for (i in dates) {
        requestMeals.add(arrayListOf(i[0], i[1], i[2], 2))
        requestMeals.add(arrayListOf(i[0], i[1], i[2], 3))
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

fun getMeal(context: Context, meals: ArrayList<ArrayList<Int>>): ArrayList<ArrayList<String>> {
    val calendar = Calendar.getInstance()
    val requestMeals = ArrayList<ArrayList<Int>>()
    val db = Room.databaseBuilder(context, MealDatabase::class.java, "mealData").build()
    val mealDataDao = db.mealDataDao()
    var wholeData = mealDataDao.getAll()
    for (meal in meals) {
        if (requestMeals.contains(meal)) continue
        calendar.set(meal[0], meal[1]-1, meal[2])
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DATE, -dayOfWeek+2)
        for (i in 0..4) {
            val date = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DATE)}-${2}"
            if (wholeData.find { it.dateType == date } == null) {
                for (j in 2..3) {
                    requestMeals.add(arrayListOf(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE), j))
                }
            }
            calendar.add(Calendar.DATE, 1)
        }
    }
    if (requestMeals.size > 0) {
        val newMeals = getMealFromServer(requestMeals, context)
        for (i in 0 until requestMeals.size) {
            mealDataDao.insert(MealData(
                "${requestMeals[i][0]}-${requestMeals[i][1]}-${requestMeals[i][2]}-${requestMeals[i][3]}",
                newMeals[i][0], newMeals[i][1], newMeals[i][2]))
        }
    }
    val resultData = ArrayList<ArrayList<String>>()
    wholeData = mealDataDao.getAll()
    for (meal in meals) {
        val date = "${meal[0]}-${meal[1]}-${meal[2]}-${meal[3]}"
        val data = wholeData.find { it.dateType == date }
        if (data != null) {
            resultData.add(arrayListOf(data.title, data.meal, data.calorie))
        } else {
            resultData.add(arrayListOf("데이터 없음", "데이터 없음", "데이터 없음"))
        }
    }
    db.close()
    return resultData
}

// meals: [[2024,8,23,2]]
fun getMealFromServer(meals: ArrayList<ArrayList<Int>>, context: Context): ArrayList<ArrayList<String>> {
    val dataStore = context.mealDataStore
    var originType = ""
    var areaCode = ""
    var schoolCode = ""
    var schoolGetType = -1
    var schoolIdLink = ""
    var schoolMealLink = ""

    val coroutineScope = CoroutineScope(Dispatchers.Unconfined).launch {
        originType = dataStore.data.first()[GetMealSettingDataStore.originType] ?: "neis"
        if (originType == "neis") {
            areaCode = dataStore.data.first()[GetMealSettingDataStore.neisAreaCode] ?: ""
            schoolCode = dataStore.data.first()[GetMealSettingDataStore.neisSchoolCode] ?: ""
        } else {
            schoolGetType = dataStore.data.first()[GetMealSettingDataStore.schoolGetType] ?: -1
            schoolIdLink = dataStore.data.first()[GetMealSettingDataStore.schoolIdLink] ?: ""
            schoolMealLink = dataStore.data.first()[GetMealSettingDataStore.schoolMealLink] ?: ""
        }
    }

    runBlocking {
        coroutineScope.join()
    }

    return if (originType == "neis") {
        getMealFromNeis(meals, areaCode, schoolCode)
    } else {
        getMealFromSchool(meals, schoolGetType, schoolIdLink, schoolMealLink)
    }
}

fun getMealFromNeis(meals: ArrayList<ArrayList<Int>>, areaCode: String, schoolCode: String): ArrayList<ArrayList<String>> {
    val group = ArrayList<ArrayList<Int>>()
    val resultMeals = ArrayList<ArrayList<String>>()
    for (i in 0..<meals.size/10) {
        group.clear()
        val meal = meals.subList(i*10, i*10+10)
        for (j in 0..4) {
            group.add(arrayListOf(meal[j*2][0], meal[j*2][1], meal[j*2][2]))
        }
        val group1Result = getNeisMeal(areaCode, schoolCode, group, 2)
        val group2Result = getNeisMeal(areaCode, schoolCode, group, 3)
        for (j in 0..4) {
            resultMeals.add(group1Result[j])
            resultMeals.add(group2Result[j])
        }
    }
    return resultMeals
}

fun getMealFromSchool(meals: ArrayList<ArrayList<Int>>, schoolGetType: Int, schoolIdLink: String, schoolMealLink: String): ArrayList<ArrayList<String>> {
    val ids = ArrayList<String>()
    var tempYear: Number = meals[0][0]
    var tempMonth: Number = meals[0][1]
    val tempDateTypes = ArrayList<ArrayList<Int>>()
    fun getIds() {
        val id = type1GetId(schoolMealLink, tempYear, tempMonth, tempDateTypes)
        ids.addAll(id)
        tempDateTypes.clear()
    }
    for (date in meals) {
        if (tempMonth != date[1]) getIds()
        tempYear = date[0]
        tempMonth = date[1]
        if (date[3] == 3 && tempDateTypes.isNotEmpty() && tempDateTypes.last()[0] == date[2]) {
            tempDateTypes.removeAt(tempDateTypes.size-1)
            tempDateTypes.add(arrayListOf(date[2], 4))
        } else {
            tempDateTypes.add(arrayListOf(date[2], date[3]))
        }
        if (date == meals.last()) getIds()
    }
    val resultMeals = ArrayList<ArrayList<String>>()
    for (id in ids) {
        if (id == "No Data") { resultMeals.add(arrayListOf("데이터 없음", "데이터 없음", "데이터 없음")) }
        else { resultMeals.add(type1GetMealData(schoolIdLink, id.toInt())) }
    }
    return resultMeals
}