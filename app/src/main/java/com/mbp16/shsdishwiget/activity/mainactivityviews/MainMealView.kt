package com.mbp16.shsdishwiget.activity.mainactivityviews

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color.parseColor
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mbp16.shsdishwiget.activity.SettingsActivity
import com.mbp16.shsdishwiget.utils.getMeals
import com.valentinilk.shimmer.shimmer
import java.util.*
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealView(activity: Activity, dataStore: DataStore<Preferences>) {
    val orientation = LocalConfiguration.current.orientation
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

    val clipboardManager = activity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val verticalScrollState = rememberScrollState()

    val margin = remember { mutableIntStateOf(8) }
    val fontSizeArray = remember { mutableStateListOf(32, 20, 18, 20) }
    val colorArray = remember { mutableStateListOf("ff171b1e", "ff4c5459", "ffe2e3e5", "ffe4bebd", "ffe2e3e5", "ff8dcae7", "cc2df07b") }

    val pickingDate = remember { mutableStateOf(false) }
    val viewingDateDelta = remember { mutableIntStateOf(0) }
    val todayWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val mealData = remember { mutableStateListOf(arrayListOf(arrayListOf("Loading", "Loading", "Loading"))) }
    val week = remember { mutableStateListOf<ArrayList<Int>>(arrayListOf(0, 0, 0)) }

    fun updateData() {
        fun exceptionHandler() {
            object : Thread() {
                override fun run() {
                    Looper.prepare()
                    Toast.makeText(activity, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                    mealData.clear()
                    for (i in 0..4) {
                        mealData.add(
                            arrayListOf(
                                arrayListOf("데이터 불러오기 실패", "데이터 불러오기 실패", "데이터 불러오기 실패"),
                                arrayListOf("데이터 불러오기 실패", "데이터 불러오기 실패", "데이터 불러오기 실패"),
                            )
                        )
                    }
                    Looper.loop()
                }
            }.start()
            try {
                Thread.sleep(4000)
            } catch (_: InterruptedException) { }
        }
        val thread = Thread {
            Runnable {
                val data = getMeals(ArrayList(week), activity)
                mealData.clear()
                mealData.addAll(data)
            }.run()
        }
        thread.setUncaughtExceptionHandler { _, _ -> exceptionHandler() }
        thread.start()
    }
    fun setWeek() {
        val newWeek = ArrayList<ArrayList<Int>>()
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
        dataStore.data.collect { preferences ->
            margin.intValue = preferences[intPreferencesKey("margin")] ?: 8
            fontSizeArray[0] = preferences[intPreferencesKey("dateFontSize")] ?: 32
            fontSizeArray[1] = preferences[intPreferencesKey("titleFontSize")] ?: 20
            fontSizeArray[2] = preferences[intPreferencesKey("mealFontSize")] ?: 18
            fontSizeArray[3] = preferences[intPreferencesKey("calorieFontSize")] ?: 20
            colorArray[0] = preferences[stringPreferencesKey("backgroundColor")] ?: "ff171b1e"
            colorArray[1] = preferences[stringPreferencesKey("cardColor")] ?: "ff4c5459"
            colorArray[2] = preferences[stringPreferencesKey("dateColor")] ?: "ffe2e3e5"
            colorArray[3] = preferences[stringPreferencesKey("titleColor")] ?: "ffe4bebd"
            colorArray[4] = preferences[stringPreferencesKey("mealColor")] ?: "ffe2e3e5"
            colorArray[5] = preferences[stringPreferencesKey("calorieColor")] ?: "ff8dcae7"
            colorArray[6] = preferences[stringPreferencesKey("todayColor")] ?: "cc2df07b"
        }
    }
    LaunchedEffect(week.toList()) {
        verticalScrollState.scrollTo(0)
    }

    if (orientation == 1) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(parseColor("#${colorArray[0]}")))
                .verticalScroll(verticalScrollState),
        ) {
            for (i in 0..<week.size) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(margin.intValue.dp)
                        .requiredHeight(400.dp),
                ) {
                    MealCard(
                        clipboardManager, margin.intValue, fontSizeArray, colorArray, week[i], mealData[i],
                        viewingDateDelta.intValue + todayWeekDay in 1..7 && i == todayWeekDay - 2, activity, true
                    )
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(parseColor("#${colorArray[0]}"))),
        ) {
            for (i in 0..<week.size) {
                Column (
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(margin.intValue.dp),
                ) {
                    MealCard(
                        clipboardManager, margin.intValue, fontSizeArray, colorArray, week[i], mealData[i],
                        viewingDateDelta.intValue + todayWeekDay in 1..7 && i == todayWeekDay - 2, activity
                    )
                }
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(margin.intValue.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .padding(margin.intValue.dp)
                .requiredWidth(50.dp)
                .requiredHeight(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = {
                viewingDateDelta.intValue -= 7
                setWeek()
            }
        ) {
            Text("<", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize)
        }
        Button(
            modifier = Modifier
                .padding(margin.intValue.dp)
                .requiredWidth(50.dp)
                .requiredHeight(50.dp),
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
                    modifier = Modifier
                        .padding(margin.intValue.dp)
                        .requiredHeight(50.dp)
                        .requiredWidth(170.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(parseColor("#${colorArray[6]}"))),
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
                modifier = Modifier
                    .padding(margin.intValue.dp)
                    .requiredWidth(50.dp)
                    .requiredHeight(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
            }
            IconButton(
                onClick = {
                    Intent(activity, SettingsActivity::class.java).also { startActivity(activity, it, null) }
                },
                modifier = Modifier
                    .padding(margin.intValue.dp)
                    .requiredWidth(50.dp)
                    .requiredHeight(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(imageVector = Icons.Outlined.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
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
                            ceil(subtractOriginal).toInt()
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
fun MealCard(clipboardManager: ClipboardManager, margin: Int, fontSizeArray: SnapshotStateList<Int>,
             colorArray: SnapshotStateList<String>, day: ArrayList<Int>, dayMeal: ArrayList<ArrayList<String>>,
             isToday: Boolean, activity: Activity, portrait: Boolean = false)  {
    Text(
        text = "${day[0]}년 ${day[1]}월 ${day[2]}일", fontSize = fontSizeArray[0].sp, fontWeight = FontWeight.Bold,
        color = (isToday).let { if (it) Color(parseColor("#${colorArray[6]}")) else Color(parseColor("#${colorArray[2]}")) },
        modifier = Modifier
            .padding(margin.dp)
            .fillMaxWidth(), textAlign = TextAlign.Center
    )
    @Composable
    fun loading(){
        Box(modifier = Modifier
            .padding(margin.dp)
            .fillMaxWidth()
            .requiredHeight(12.dp)
            .background(Color(parseColor("#${colorArray[3]}"))))
        for (j in 0..6) {
            Box(modifier = Modifier
                .padding(margin.dp)
                .fillMaxWidth()
                .requiredHeight(12.dp)
                .background(Color(parseColor("#${colorArray[4]}"))))
        }
        Box(modifier = Modifier
            .padding(margin.dp)
            .fillMaxWidth()
            .requiredHeight(12.dp)
            .background(Color(parseColor("#${colorArray[5]}"))))
    }
    @Composable
    fun content(meal: ArrayList<String>) {
        Text(text = meal[0], fontSize = fontSizeArray[1].sp, fontWeight = FontWeight.Bold,
            color = Color(parseColor("#${colorArray[3]}")), modifier = Modifier.padding(margin.dp))
        Text(text = meal[1].replace(",", "\n").replace(" ", ""), fontSize = fontSizeArray[2].sp,
            color = Color(parseColor("#${colorArray[4]}")), modifier = Modifier.padding(margin.dp), fontWeight = FontWeight.Bold)
        Text(text = meal[2], fontSize = fontSizeArray[3].sp,
            color = Color(parseColor("#${colorArray[5]}")),
            modifier = Modifier.padding(margin.dp), fontWeight = FontWeight.Bold)
    }
    if (portrait) {
        Row (modifier = Modifier.fillMaxWidth()) {
            for (i in dayMeal) {
                if (i[0] == "Loading") {
                    Card(modifier = Modifier
                        .padding(margin.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f)
                        .shimmer(), shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor=Color(parseColor("#${colorArray[1]}")))
                    ) { loading() }
                } else {
                    Card(shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor=Color(parseColor("#${colorArray[1]}"))),
                        modifier = Modifier
                            .padding(margin.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable {
                                clipboardManager.setPrimaryClip(
                                    ClipData.newPlainText(
                                        "meal",
                                        i[1]
                                            .replace(",", "\n")
                                            .replace(" ", "")
                                    )
                                )
                                if (Build.VERSION.SDK_INT <= 32) {
                                    Toast
                                        .makeText(activity, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }) { content(i) }
                }
            }
        }
    } else {
        Column (modifier = Modifier.fillMaxWidth()) {
            for (i in dayMeal) {
                if (i[0] == "Loading") {
                    Card(modifier = Modifier
                        .padding(margin.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f)
                        .shimmer(), shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor=Color(parseColor("#${colorArray[1]}")))
                    ) { loading() }
                } else {
                    Card(shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor=Color(parseColor("#${colorArray[1]}"))),
                        modifier = Modifier
                            .padding(margin.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable {
                                clipboardManager.setPrimaryClip(
                                    ClipData.newPlainText(
                                        "meal",
                                        i[1]
                                            .replace(",", "\n")
                                            .replace(" ", "")
                                    )
                                )
                                if (Build.VERSION.SDK_INT <= 32) {
                                    Toast
                                        .makeText(activity, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }) { content(i) }
                }
            }
        }
    }
}


