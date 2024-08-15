package com.mbp16.shsdishwiget.activity.settingsactivityviews

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.mbp16.shsdishwiget.activity.MainActivity
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore.Companion.dataStore
import kotlinx.coroutines.launch

class GetMealSettingDataStore {
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "getMealSettingDataStore")

        val schoolName = stringPreferencesKey("schoolName")
        val originType = stringPreferencesKey("originType")
        val neisLink = stringPreferencesKey("neisLink")
        val schoolIdLink = stringPreferencesKey("schoolIdLink")
        val schoolMealLink = stringPreferencesKey("schoolMealLink")
    }
}

@Composable
fun GetMealSettingView(activity: Activity) {
    val dataStore = (LocalContext.current).dataStore
    val coroutineScope = rememberCoroutineScope()

    val schoolName = remember { mutableStateOf("") }
    val originType = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        dataStore.data.collect { preferences ->
            schoolName.value = preferences[GetMealSettingDataStore.schoolName] ?: ""
            when (preferences[GetMealSettingDataStore.originType] ?: "") {
                "neis" -> { originType.value = "나이스 Open API" }
                "school" -> { originType.value = "학교 사이트 크롤링" }
            }
        }
    }

    fun resetData() {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[GetMealSettingDataStore.schoolName] = ""
                preferences[GetMealSettingDataStore.originType] = ""
            }
        }
        val intent = Intent(activity, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        activity.finish()
    }

    Text(text = "학교/급식 출처 설정", modifier = Modifier.fillMaxWidth().padding(20.dp),
        fontSize = MaterialTheme.typography.displaySmall.fontSize)
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(text = "학교")
            Text(text = schoolName.value)
        }
        Divider()
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(text = "출처")
            Text(text = originType.value)
        }
        Divider()
        Text(
            text="학교 정보 재설정",
            modifier = Modifier.clickable { resetData() }.fillMaxWidth().padding(20.dp)
        )
    }
}
