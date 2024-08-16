package com.mbp16.shsdishwiget.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mbp16.shsdishwiget.activity.mainactivityviews.InitialSettingsView
import com.mbp16.shsdishwiget.activity.mainactivityviews.MealView
import com.mbp16.shsdishwiget.activity.mainactivityviews.UpdateView
import com.mbp16.shsdishwiget.activity.settingsactivityviews.MainActivitySettingDataStore
import com.mbp16.shsdishwiget.activity.settingsactivityviews.MainActivitySettingDataStore.Companion.dataStore
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import com.mbp16.shsdishwiget.utils.Release
import com.mbp16.shsdishwiget.utils.getUpdate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dataStore = (LocalContext.current).dataStore
            val dataFirmed = remember { mutableStateOf(false) }
            val dialogViewing = remember { mutableStateOf(false) }
            var result: Any = false
            LaunchedEffect(Unit) {
                dataStore.data.collect { preferences ->
                    val updateAuto = preferences[MainActivitySettingDataStore.updateAuto] ?: true
                    if (updateAuto) {
                        val thread = Thread() {
                            result = getUpdate(this@MainActivity)
                            if (result != false) {dialogViewing.value = true}
                        }
                        thread.setUncaughtExceptionHandler { _, _ -> dialogViewing.value = false }
//                        thread.start()
                    }
                    dataFirmed.value = preferences[MainActivitySettingDataStore.margin] != null
                }
            }
            SHSDishWigetTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (dialogViewing.value) {
                        UpdateView(dialogViewing, result as Release, this)
                    }
                    if (dataFirmed.value) {
                        MealView(this, dataStore)
                    } else {
                        InitialSettingsView(this, dataStore)
                    }
                }
            }
        }
    }
}
