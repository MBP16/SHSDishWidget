package com.mbp16.shsdishwiget.activity.settingsactivityviews

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.room.Room
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore.Companion.mealDataStore
import com.mbp16.shsdishwiget.utils.MealDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class GetMealSettingDataStore {
    companion object {
        val Context.mealDataStore: DataStore<Preferences> by preferencesDataStore(name = "getMealSettingDataStore")

        val schoolName = stringPreferencesKey("schoolName")
        val originType = stringPreferencesKey("originType")
        val neisAreaCode = stringPreferencesKey("neisAreaCode")
        val neisSchoolCode = stringPreferencesKey("neisSchoolCode")
        val schoolGetType = intPreferencesKey("schoolGetType")
        val schoolIdLink = stringPreferencesKey("schoolIdLink")
        val schoolMealLink = stringPreferencesKey("schoolMealLink")
    }
}

@Composable
fun GetMealSettingView(activity: Activity) {
    val dataStore = activity.mealDataStore
    val coroutineScope = rememberCoroutineScope()

    val schoolName = remember { mutableStateOf("") }
    val originType = remember { mutableStateOf("") }

    val checkDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dataStore.data.first().let { preferences ->
            schoolName.value = preferences[GetMealSettingDataStore.schoolName] ?: ""
            when (preferences[GetMealSettingDataStore.originType] ?: "") {
                "neis" -> { originType.value = "나이스 Open API" }
                "school" -> { originType.value = "학교 사이트 크롤링" }
            }
        }
    }

    fun resetData() {
        val db = Room.databaseBuilder(activity, MealDatabase::class.java, "mealData").allowMainThreadQueries().build()
        val mealDataDao = db.mealDataDao()

        coroutineScope.launch {
            mealDataDao.deleteAll()
            dataStore.edit { preferences ->
                preferences.remove(GetMealSettingDataStore.schoolName)
                preferences.remove(GetMealSettingDataStore.originType)
                preferences.remove(GetMealSettingDataStore.neisAreaCode)
                preferences.remove(GetMealSettingDataStore.neisSchoolCode)
                preferences.remove(GetMealSettingDataStore.schoolGetType)
                preferences.remove(GetMealSettingDataStore.schoolIdLink)
                preferences.remove(GetMealSettingDataStore.schoolMealLink)
            }
            exitProcess(0)
        }
    }

    if (checkDialog.value) {
        AlertDialog(
            onDismissRequest = { checkDialog.value = false },
            title = { Text("학교 정보 재설정") },
            text = { Text("학교 정보를 재설정하면 기존의 모든 데이터가 삭제됩니다. 계속하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        resetData()
                        checkDialog.value = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(
                    onClick = { checkDialog.value = false },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text("취소")
                }
            }
        )
    }

    Text(text = "학교/급식 출처 설정", modifier = Modifier.fillMaxWidth().padding(20.dp),
        fontSize = MaterialTheme.typography.displaySmall.fontSize)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "학교")
            Text(text = schoolName.value)
        }
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "출처")
            Text(text = originType.value)
        }
        HorizontalDivider()
        Text(
            text="학교 정보 재설정",
            modifier = Modifier.clickable { checkDialog.value = true }.fillMaxWidth().padding(20.dp)
        )
    }
}
