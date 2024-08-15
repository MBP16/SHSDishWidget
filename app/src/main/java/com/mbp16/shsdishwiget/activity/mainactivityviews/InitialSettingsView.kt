package com.mbp16.shsdishwiget.activity.mainactivityviews

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore

@Composable
fun InitialSettingsView(activity: Activity, dataStore: DataStore<Preferences>) {
    val schoolName = remember { mutableStateOf("") }
    val originType = remember { mutableStateOf("") }
    val neisLink = remember { mutableStateOf("") }
    val schoolIdLink = remember { mutableStateOf("") }
    val schoolMealLink = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        dataStore.data.collect { preferences ->
            schoolName.value = preferences[GetMealSettingDataStore.schoolName] ?: ""
            originType.value = preferences[GetMealSettingDataStore.originType] ?: ""
            neisLink.value = preferences[GetMealSettingDataStore.neisLink] ?: ""
            schoolIdLink.value = preferences[GetMealSettingDataStore.schoolIdLink] ?: ""
            schoolMealLink.value = preferences[GetMealSettingDataStore.schoolMealLink] ?: ""
        }
    }


}