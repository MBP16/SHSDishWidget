package com.mbp16.shsdishwiget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
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
            }
        }
    }

    fun saveData() {
        val resultValue = Intent()
        coroutineScope.launch {
            try {
                updateAppWidgetState(activity, glanceId) {
                    it[booleanPreferencesKey("showNextWeek")] = showNextWeekChecked.value
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

    Column(modifier = Modifier.fillMaxSize()) {
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    saveData()
                },
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(text = "저장")
            }
        }
    }
}