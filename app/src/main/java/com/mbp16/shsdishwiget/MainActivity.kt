package com.mbp16.shsdishwiget

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import com.valentinilk.shimmer.shimmer
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conMan = getSystemService(ConnectivityManager::class.java)
        val networkInfo = conMan.activeNetwork
        val caps = conMan.getNetworkCapabilities(networkInfo)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (caps == false || caps == null) {
            setContent {
                SHSDishWigetTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "인터넷 연결이 필요합니다.",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            return
        } else {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealView() {
    val pickingDate = remember { mutableStateOf(false) }
    val viewingDateDelta = remember { mutableIntStateOf(0) }
    val todayWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val mealData = remember { mutableStateListOf(arrayListOf(arrayListOf("Loading", "Loading", "Loading"))) }
    val week = remember { mutableStateListOf<ArrayList<Number>>(arrayListOf(0, 0, 0)) }
    fun updateData() {
        Thread {
            Runnable {
                val data = GetMealData(ArrayList(week))
                mealData.clear()
                mealData.addAll(data)
            }.run()
        }.start()
    }
    fun setWeek() {
        val newWeek = ArrayList<ArrayList<Number>>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, viewingDateDelta.intValue)
        val viewingWeekDay = calendar.get(Calendar.DAY_OF_WEEK)
        for (i in 2..6) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, viewingDateDelta.intValue + i - viewingWeekDay)
            newWeek.add(arrayListOf(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
        }
        if (week[0] != newWeek[0]) {
            week.clear()
            week.addAll(newWeek)
            mealData.clear()
            for (i in 0..4) {
                mealData.add(
                    arrayListOf(
                        arrayListOf("Loading", "Loading", "Loading"),
                        arrayListOf("Loading", "Loading", "Loading"),
                    )
                )
            }
            updateData()
        }
    }

    LaunchedEffect(Unit) {
        setWeek()
    }
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        for (i in 0..<week.size) {
            MealCard(week[i], mealData[i], viewingDateDelta.intValue + todayWeekDay in 1..7 && i == todayWeekDay - 2)
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
            }
        ) {
            Text(">", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize)
        }
    }
    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    )
    {
        Row (
            modifier = Modifier.offset(x = (-8).dp, y = (-8).dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewingDateDelta.intValue + todayWeekDay !in 1..7) {
                Button(
                    modifier = Modifier.padding(8.dp).requiredHeight(50.dp).requiredWidth(170.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC2DF07B)),
                    onClick = {
                        viewingDateDelta.intValue = 0
                        setWeek()
                    }
                ) {
                    Text(
                        "오늘로 돌아가기",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                }
            }
            IconButton(
                onClick = {
                    pickingDate.value = !pickingDate.value
                },
                modifier = Modifier.padding(8.dp).requiredWidth(50.dp).requiredHeight(50.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
            }
        }
    }
    if (pickingDate.value) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, viewingDateDelta.intValue)
        val datePickerState = rememberDatePickerState(
            yearRange = 2022..2030,
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = calendar.timeInMillis + 32400000
        )
        DatePickerDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        pickingDate.value = false
                        viewingDateDelta.intValue = datePickerState.selectedDateMillis?.let {
                            val subtractOriginal = (it - 32400000 - Calendar.getInstance().timeInMillis.toDouble()) / 86400000
                            Math.ceil(subtractOriginal).toInt()
                        } ?: 0
                        setWeek()
                    },
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(
                    onClick = { pickingDate.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("취소")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
            )
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
                    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().fillMaxHeight().weight(1f).shimmer(), shape = MaterialTheme.shapes.medium) {
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
                        Text(text = i[1].replace(",", "\n").replace(" ", ""), style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp))
                        Text(text = i[2], style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp), color= MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
