package com.mbp16.shsdishwiget.activity.widgetconfigure

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import com.mbp16.shsdishwiget.activity.ColorChangingRow
import com.mbp16.shsdishwiget.activity.TextStyleChange
import com.mbp16.shsdishwiget.glance.MealWidget
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MealWidgetConfigureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHSDishWigetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MealWidgetConfigureScreen(this)
                }
            }
        }
    }
}

@Composable
fun MealWidgetConfigureScreen(activity: Activity) {
    val margin = remember { mutableIntStateOf(8) }

    val changeLunch = remember { mutableIntStateOf(1800) }
    val changeDinner = remember { mutableIntStateOf(1300) }

    val dateFontSize = remember { mutableIntStateOf(28) }
    val titleFontSize = remember { mutableIntStateOf(20) }
    val mealFontSize = remember { mutableIntStateOf(18) }
    val calorieFontSize = remember { mutableIntStateOf(20) }

    val backgroundColor = remember { mutableStateOf("ff171b1e") }
    val dateColor = remember { mutableStateOf("ffe2e3e5") }
    val titleColor = remember { mutableStateOf("ffe4bebd") }
    val mealColor = remember { mutableStateOf("ffe2e3e5") }
    val calorieColor = remember { mutableStateOf("ff8dcae7") }

    val viewingDialog = remember { mutableStateOf("") }
    val viewingError = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val appWidgetId = activity.intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    val glanceAppWidgetManager = GlanceAppWidgetManager(activity)
    val glanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)

    LaunchedEffect(Unit) {
        CoroutineScope(coroutineContext).launch {
            MealWidget().getAppWidgetState<Preferences>(activity, glanceId).let {
                margin.intValue = it[intPreferencesKey("margin")] ?: 8
                changeLunch.intValue = it[intPreferencesKey("changeLunch")] ?: 1800
                changeDinner.intValue = it[intPreferencesKey("changeDinner")] ?: 1300
                dateFontSize.intValue = it[intPreferencesKey("dateFontSize")] ?: 28
                titleFontSize.intValue = it[intPreferencesKey("titleFontSize")] ?: 20
                mealFontSize.intValue = it[intPreferencesKey("mealFontSize")] ?: 18
                calorieFontSize.intValue = it[intPreferencesKey("calorieFontSize")] ?: 20
                backgroundColor.value = it[stringPreferencesKey("backgroundColor")] ?: "ff171b1e"
                dateColor.value = it[stringPreferencesKey("dateColor")] ?: "ffe2e3e5"
                titleColor.value = it[stringPreferencesKey("titleColor")] ?: "ffe4bebd"
                mealColor.value = it[stringPreferencesKey("mealColor")] ?: "ffe2e3e5"
                calorieColor.value = it[stringPreferencesKey("calorieColor")] ?: "ff8dcae7"
            }
        }
    }

    fun restoreData() {
        margin.intValue = 8
        changeLunch.intValue = 1800
        changeDinner.intValue = 1300
        dateFontSize.intValue = 28
        titleFontSize.intValue = 20
        mealFontSize.intValue = 18
        calorieFontSize.intValue = 20
        backgroundColor.value = "ff171b1e"
        dateColor.value = "ffe2e3e5"
        titleColor.value = "ffe4bebd"
        mealColor.value = "ffe2e3e5"
        calorieColor.value = "ff8dcae7"
    }

    fun saveData() {
        if (changeLunch.intValue <= changeDinner.intValue) {
            viewingError.value = true
            return
        }
        val resultValue = Intent()
        coroutineScope.launch {
            try {
                updateAppWidgetState(activity, glanceId) {
                    it[intPreferencesKey("margin")] = margin.intValue
                    it[intPreferencesKey("changeLunch")] = changeLunch.intValue
                    it[intPreferencesKey("changeDinner")] = changeDinner.intValue
                    it[intPreferencesKey("dateFontSize")] = dateFontSize.intValue
                    it[intPreferencesKey("titleFontSize")] = titleFontSize.intValue
                    it[intPreferencesKey("mealFontSize")] = mealFontSize.intValue
                    it[intPreferencesKey("calorieFontSize")] = calorieFontSize.intValue
                    it[stringPreferencesKey("backgroundColor")] = backgroundColor.value
                    it[stringPreferencesKey("dateColor")] = dateColor.value
                    it[stringPreferencesKey("titleColor")] = titleColor.value
                    it[stringPreferencesKey("mealColor")] = mealColor.value
                    it[stringPreferencesKey("calorieColor")] = calorieColor.value
                }
                MealWidget().update(activity, glanceId)
                Toast.makeText(activity, "저장 완료", Toast.LENGTH_SHORT).show()
                activity.setResult(Activity.RESULT_OK, resultValue)
                activity.finish()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(activity, "저장 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun formattingTime(time: Int): String {
        val hour = time / 100
        val min = String.format("%02d", time % 100)
        if (hour == 0) {
            return "오전 12:${time % 100}"
        } else if (hour < 12) {
            return "오전 $hour:$min"
        } else if (hour == 12) {
            return "오후 12:$min"
        } else {
            return "오후 ${hour - 12}:$min"
        }
    }

    if (viewingDialog.value == "changeLunch") {
        TimeDialog(viewingDialog, changeLunch)
    } else if (viewingDialog.value == "changeDinner") {
        TimeDialog(viewingDialog, changeDinner)
    }

    if (viewingError.value) {
        AlertDialog(
            onDismissRequest = { viewingError.value = false },
            title = { Text("오류 발생") },
            text = { Text("다음 날 점심으로의 변경 시간이 저녁으로의 변경 시간보다 빠를 수 없습니다.") },
            confirmButton = {
                Button(
                    onClick = { viewingError.value = false }
                ) {
                    Text("확인")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Column() {
            Text(
                text = "전체 설정",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .clickable {
                        viewingDialog.value = "changeLunch"
                    }
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "다음날 점심으로 변경되는 시간",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                Button(onClick = { viewingDialog.value = "changeLunch" }) {
                    Text(
                        text = formattingTime(changeLunch.intValue),
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }
            }
            Row(
                modifier = Modifier
                    .clickable {
                        viewingDialog.value = "changeDinner"
                    }
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "저녁으로 변경되는 시간",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                Button(onClick = { viewingDialog.value = "changeDinner" }) {
                    Text(
                        text = formattingTime(changeDinner.intValue),
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp, 16.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text="여백",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                Slider(
                    value = margin.intValue.toFloat(),
                    onValueChange = {
                        margin.intValue = it.toInt()
                    },
                    valueRange = 0.0F..32.0F,
                    steps = 31,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                Text(
                    text = margin.intValue.toString(),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    textAlign = TextAlign.Center
                )
            }
            ColorChangingRow(backgroundColor)
        }
        Divider()
        TextStyleChange("날짜 표기 설정", dateFontSize, dateColor)
        Divider()
        TextStyleChange("급식 제목 표기 설정", titleFontSize, titleColor)
        Divider()
        TextStyleChange("급식 표기 설정", mealFontSize, mealColor)
        Divider()
        TextStyleChange("칼로리 표기 설정", calorieFontSize, calorieColor)
        Column(modifier = Modifier.fillMaxWidth().requiredHeight(72.dp)) {}
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Row {
            FloatingActionButton(
                onClick = { restoreData() },
                containerColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp, 16.dp, 0.dp, 16.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
            }
            FloatingActionButton(
                onClick = { saveData() },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Done, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(viewingDialog: MutableState<String>, time: MutableIntState) {
    val timePickerState = rememberTimePickerState(
        initialHour = time.intValue / 100,
        initialMinute = time.intValue % 100
    )

    Dialog(
        onDismissRequest = { viewingDialog.value = "" },
    ) {
        Card(shape = RoundedCornerShape(8.dp)) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        time.intValue = timePickerState.hour * 100 + timePickerState.minute
                        viewingDialog.value = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("확인")
                }
            }
        }
    }
}
