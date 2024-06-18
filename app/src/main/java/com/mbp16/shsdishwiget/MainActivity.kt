package com.mbp16.shsdishwiget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import java.util.Calendar
import kotlin.collections.ArrayList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHSDishWigetTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MealView()
                }
            }
        }
    }
}

@Composable
fun MealView() {
    val cal = Calendar.getInstance()
    val mealData = remember { mutableStateListOf<ArrayList<ArrayList<String>>>() }
    val week = ArrayList<ArrayList<Number>>()
    for (i in 2..6) {
        val todayWeekDay = cal.get(Calendar.DAY_OF_WEEK)
        val day = cal.get(Calendar.DAY_OF_MONTH) - todayWeekDay + i
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        week.add(arrayListOf(year, month, day))
        mealData.add(arrayListOf(arrayListOf("Loading", "Loading", "Loading")))
    }
    fun updateData() {
        Thread {
            Runnable {
                val data = GetMealData(week)
                mealData.clear()
                mealData.addAll(data)
            }.run()
        }.start()
    }
    LaunchedEffect(key1 = mealData) {
        updateData()
    }
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        for (i in 0..4) {
            MealCard(week[i], mealData[i])
        }
    }
}

@Composable
fun MealCard(day: ArrayList<Number>, dayMeal: ArrayList<ArrayList<String>>) {
    Card(
        modifier = Modifier.padding(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Text(text = "${day[0]}년 ${day[1]}월 ${day[2]}일", style = MaterialTheme.typography.titleSmall)
            Column {
                for (i in dayMeal) {
                    Column {
                        Text(text = i[0], style = MaterialTheme.typography.bodyLarge)
                        Text(text = i[1].replace(",", "\n"), style = MaterialTheme.typography.bodySmall)
                        Text(text = i[2], style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
