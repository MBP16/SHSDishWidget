package com.mbp16.shsdishwiget.activity.widgetconfigure

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import com.mbp16.shsdishwiget.activity.ColorChangingRow
import com.mbp16.shsdishwiget.activity.TextStyleChange
import com.mbp16.shsdishwiget.glance.MealMultipleWidget
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MealMultipleWidgetConfigureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHSDishWigetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MealMultipleWidgetConfigureScreen(this)
                }
            }
        }
    }
}

@Composable
fun MealMultipleWidgetConfigureScreen(activity: Activity) {
    val showNextWeekChecked = remember { mutableStateOf(false) }
    val margin = remember { mutableIntStateOf(8) }

    val dateFontSize = remember { mutableIntStateOf(24) }
    val titleFontSize = remember { mutableIntStateOf(20) }
    val mealFontSize = remember { mutableIntStateOf(14) }
    val calorieFontSize = remember { mutableIntStateOf(16) }

    val backgroundColor = remember { mutableStateOf("ff171b1e") }
    val dateColor = remember { mutableStateOf("ffe2e3e5") }
    val titleColor = remember { mutableStateOf("ffe4bebd") }
    val mealColor = remember { mutableStateOf("ffe2e3e5") }
    val calorieColor = remember { mutableStateOf("ff8dcae7") }

    val coroutineScope = rememberCoroutineScope()
    val appWidgetId = activity.intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    val glanceAppWidgetManager = GlanceAppWidgetManager(activity)
    val glanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)

    LaunchedEffect(Unit) {
        CoroutineScope(coroutineContext).launch {
            MealMultipleWidget().getAppWidgetState<Preferences>(activity, glanceId).let {
                showNextWeekChecked.value = it[booleanPreferencesKey("showNextWeek")] ?: false
                margin.intValue = it[intPreferencesKey("margin")] ?: 8
                dateFontSize.intValue = it[intPreferencesKey("dateFontSize")] ?: 24
                titleFontSize.intValue = it[intPreferencesKey("titleFontSize")] ?: 20
                mealFontSize.intValue = it[intPreferencesKey("mealFontSize")] ?: 14
                calorieFontSize.intValue = it[intPreferencesKey("calorieFontSize")] ?: 16
                backgroundColor.value = it[stringPreferencesKey("backgroundColor")] ?: "ff171b1e"
                dateColor.value = it[stringPreferencesKey("dateColor")] ?: "ffe2e3e5"
                titleColor.value = it[stringPreferencesKey("titleColor")] ?: "ffe4bebd"
                mealColor.value = it[stringPreferencesKey("mealColor")] ?: "ffe2e3e5"
                calorieColor.value = it[stringPreferencesKey("calorieColor")] ?: "ff8dcae7"
            }
        }
    }

    fun saveData() {
        val resultValue = Intent()
        coroutineScope.launch {
            try {
                updateAppWidgetState(activity, glanceId) {
                    it[booleanPreferencesKey("showNextWeek")] = showNextWeekChecked.value
                    it[intPreferencesKey("margin")] = margin.intValue
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
                MealMultipleWidget().update(activity, glanceId)
                Toast.makeText(activity, "저장 완료", Toast.LENGTH_SHORT).show()
                activity.setResult(Activity.RESULT_OK, resultValue)
                activity.finish()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(activity, "저장 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column() {
            Text(
                text = "전체 설정",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text="지난 요일은 다음 주 급식 미리 보여주기",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                Switch(
                    checked = showNextWeekChecked.value,
                    onCheckedChange = {
                        showNextWeekChecked.value = it
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp, 16.dp, 16.dp),
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
                    steps = 33,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
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
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                saveData()
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(imageVector = Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.surface)
        }
    }
}
