package com.mbp16.shsdishwiget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import com.valentinilk.shimmer.shimmer
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
    val viewingDateDelta = remember { mutableIntStateOf(0) }
    val todayWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val mealData = remember { mutableStateListOf<ArrayList<ArrayList<String>>>()}
    val week = remember { mutableStateListOf<ArrayList<Number>>() }
    fun setWeek() {
        week.clear()
        mealData.clear()
        for (i in 2..6) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, viewingDateDelta.intValue + i - todayWeekDay)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH) + 1
            val year = cal.get(Calendar.YEAR)
            week.add(arrayListOf(year, month, day))
            mealData.add(
                arrayListOf(
                    arrayListOf("Loading", "Loading", "Loading"),
                    arrayListOf("Loading", "Loading", "Loading"),
                )
            )
        }
    }
    fun updateData() {
        Thread {
            Runnable {
                val data = GetMealData(ArrayList(week))
                mealData.clear()
                mealData.addAll(data)
            }.run()
        }.start()
    }
    LaunchedEffect(Unit) {
        setWeek()
        updateData()
    }
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        for (i in 0..<week.size) {
            MealCard(week[i], mealData[i], viewingDateDelta.intValue == 0 && i == todayWeekDay - 2)
        }
    }
    Row(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier.padding(8.dp).requiredWidth(50.dp).requiredHeight(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = {
                viewingDateDelta.intValue -= 7
                setWeek()
                updateData()
            }
        ) {
            Text("<", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize)
        }
        Button(
            modifier = Modifier.padding(8.dp).requiredWidth(50.dp).requiredHeight(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = {
                viewingDateDelta.intValue += 7
                setWeek()
                updateData()
            }
        ) {
            Text(">", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize)
        }
    }
    if (viewingDateDelta.intValue != 0) {
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        )
        {
            Button(
                modifier = Modifier.padding(8.dp).requiredHeight(50.dp).requiredWidth(170.dp).offset(x = (-8).dp, y = (-8).dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC2DF07B)),
                onClick = {
                    viewingDateDelta.intValue = 0
                    setWeek()
                    updateData()
                }
            ) {
                Text("오늘로 돌아가기", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
            }
        }
    }
}

@Composable
fun RowScope.MealCard(day: ArrayList<Number>, dayMeal: ArrayList<ArrayList<String>>, isToday: Boolean) {
    Column (
        modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp),
    ) {
        Text(
            text = "${day[0]}년 ${day[1]}월 ${day[2]}일", style = MaterialTheme.typography.displaySmall,
            color = (isToday).let { if (it) Color(0xCC2DF07B) else MaterialTheme.colorScheme.onSurface },
            modifier = Modifier.padding(8.dp).fillMaxWidth(), textAlign = TextAlign.Center
        )
        Column (modifier = Modifier.fillMaxWidth()) {
            for (i in dayMeal) {
                if (i[0] == "Loading") {
                    Card(modifier = Modifier.padding(8.dp).shimmer().fillMaxWidth().fillMaxHeight().weight(1f), shape = MaterialTheme.shapes.medium) {
                        Box(modifier = Modifier.padding(8.dp).fillMaxWidth().requiredHeight(12.dp).background(MaterialTheme.colorScheme.error))
                        for (j in 0..6) {
                            Box(modifier = Modifier.padding(8.dp).fillMaxWidth().requiredHeight(12.dp).background(MaterialTheme.colorScheme.onSurface))
                        }
                        Box(modifier = Modifier.padding(8.dp).fillMaxWidth().requiredHeight(12.dp).background(MaterialTheme.colorScheme.primary))
                    }
                } else {
                    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().fillMaxHeight().weight(1f), shape = MaterialTheme.shapes.medium) {
                        Text(text = i[0], style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.error)
                        Text(text = i[1].replace(",", "\n"), style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp))
                        Text(text = i[2], style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp), color= MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
