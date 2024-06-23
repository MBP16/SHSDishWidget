package com.mbp16.shsdishwiget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import kotlinx.coroutines.launch

class MealMultipleWidgetConfigureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val pref = getSharedPreferences("meal_multiple_widget", Activity.MODE_PRIVATE)
        val editor = pref.edit()

        super.onCreate(savedInstanceState)
        setContent {
            SHSDishWigetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MealMultipleWidgetConfigureScreen(pref, editor, this)
                }
            }
        }
    }
}

@Composable
fun MealMultipleWidgetConfigureScreen(pref: SharedPreferences, editor: SharedPreferences.Editor, activity: Activity) {
    val showNextWeekChecked = remember { mutableStateOf(pref.getBoolean("showNextWeek", false)) }
    val coroutineScope = rememberCoroutineScope()

    fun saveData() {
        editor.putBoolean("showNextWeek", showNextWeekChecked.value)
        editor.apply()
        Toast.makeText(activity, "저장 완료", Toast.LENGTH_SHORT).show()
        val appWidgetId = activity.intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        coroutineScope.launch {
            activity.setResult(Activity.RESULT_OK, resultValue)
            MealMultipleWidget().updateAll(activity)
            activity.finish()
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