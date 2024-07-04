package com.mbp16.shsdishwiget.activity.settingsactivityviews

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mbp16.shsdishwiget.activity.ColorChangingRow
import com.mbp16.shsdishwiget.activity.TextStyleChange
import com.mbp16.shsdishwiget.activity.settingsactivityviews.MainActivitySettingDataStore.Companion.dataStore
import kotlinx.coroutines.launch

class MainActivitySettingDataStore {
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mainActivitySettings")

        val margin = intPreferencesKey("margin")

        val dateFontSize = intPreferencesKey("dateFontSize")
        val titleFontSize = intPreferencesKey("titleFontSize")
        val mealFontSize = intPreferencesKey("mealFontSize")
        val calorieFontSize = intPreferencesKey("calorieFontSize")

        val backgroundColor = stringPreferencesKey("backgroundColor")
        val cardColor = stringPreferencesKey("cardColor")
        val dateColor = stringPreferencesKey("dateColor")
        val titleColor = stringPreferencesKey("titleColor")
        val mealColor = stringPreferencesKey("mealColor")
        val calorieColor = stringPreferencesKey("calorieColor")
        val todayColor = stringPreferencesKey("todayColor")
    }
}

@Composable
fun MainScreenSettingView(activity: Activity) {
    val dataStore = (LocalContext.current).dataStore
    val coroutineScope = rememberCoroutineScope()

    val margin = remember { mutableIntStateOf(8) }

    val dateFontSize = remember { mutableIntStateOf(32) }
    val titleFontSize = remember { mutableIntStateOf(20) }
    val mealFontSize = remember { mutableIntStateOf(18) }
    val calorieFontSize = remember { mutableIntStateOf(20) }

    val backgroundColor = remember { mutableStateOf("ff171b1e") }
    val cardColor = remember { mutableStateOf("ff4c5459") }
    val dateColor = remember { mutableStateOf("ffe2e3e5") }
    val titleColor = remember { mutableStateOf("ffe4bebd") }
    val mealColor = remember { mutableStateOf("ffe2e3e5") }
    val calorieColor = remember { mutableStateOf("ff8dcae7") }
    val todayColor = remember { mutableStateOf("cc2df07b") }

    LaunchedEffect(Unit) {
        dataStore.data.collect { preferences ->
            margin.intValue = preferences[MainActivitySettingDataStore.margin] ?: 8

            dateFontSize.intValue = preferences[MainActivitySettingDataStore.dateFontSize] ?: 32
            titleFontSize.intValue = preferences[MainActivitySettingDataStore.titleFontSize] ?: 20
            mealFontSize.intValue = preferences[MainActivitySettingDataStore.mealFontSize] ?: 18
            calorieFontSize.intValue = preferences[MainActivitySettingDataStore.calorieFontSize] ?: 20

            backgroundColor.value = preferences[MainActivitySettingDataStore.backgroundColor] ?: "ff171b1e"
            cardColor.value = preferences[MainActivitySettingDataStore.cardColor] ?: "ff4c5459"
            dateColor.value = preferences[MainActivitySettingDataStore.dateColor] ?: "ffe2e3e5"
            titleColor.value = preferences[MainActivitySettingDataStore.titleColor] ?: "ffe4bebd"
            mealColor.value = preferences[MainActivitySettingDataStore.mealColor] ?: "ffe2e3e5"
            calorieColor.value = preferences[MainActivitySettingDataStore.calorieColor] ?: "ff8dcae7"
            todayColor.value = preferences[MainActivitySettingDataStore.todayColor] ?: "cc2df07b"
        }
    }

    fun saveData() {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[MainActivitySettingDataStore.margin] = margin.intValue
                preferences[MainActivitySettingDataStore.dateFontSize] = dateFontSize.intValue
                preferences[MainActivitySettingDataStore.titleFontSize] = titleFontSize.intValue
                preferences[MainActivitySettingDataStore.mealFontSize] = mealFontSize.intValue
                preferences[MainActivitySettingDataStore.calorieFontSize] = calorieFontSize.intValue
                preferences[MainActivitySettingDataStore.backgroundColor] = backgroundColor.value
                preferences[MainActivitySettingDataStore.cardColor] = cardColor.value
                preferences[MainActivitySettingDataStore.dateColor] = dateColor.value
                preferences[MainActivitySettingDataStore.titleColor] = titleColor.value
                preferences[MainActivitySettingDataStore.mealColor] = mealColor.value
                preferences[MainActivitySettingDataStore.calorieColor] = calorieColor.value
                preferences[MainActivitySettingDataStore.todayColor] = todayColor.value
            }
            activity.finish()
        }
    }

    Text(text = "메인화면 설정", modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
        fontSize = MaterialTheme.typography.displaySmall.fontSize)
    Column() {
        Text(
            text = "전체 설정",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
    Column {
        Text(
            text="카드 설정",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.padding(16.dp)
        )
        ColorChangingRow(cardColor)
    }
    Divider()
    TextStyleChange("날짜 표기 설정", dateFontSize, dateColor)
    Divider()
    TextStyleChange("급식 제목 표기 설정", titleFontSize, titleColor)
    Divider()
    TextStyleChange("급식 표기 설정", mealFontSize, mealColor)
    Divider()
    TextStyleChange("칼로리 표기 설정", calorieFontSize, calorieColor)
    Divider()
    Column {
        Text(
            text="오늘 날짜 표기 설정",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.padding(16.dp)
        )
        ColorChangingRow(todayColor)
    }
    Row {
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                saveData()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(16.dp),
        ) {
            Text("저장")
        }
    }
}