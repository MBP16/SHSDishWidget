package com.mbp16.shsdishwiget.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mbp16.shsdishwiget.activity.settingsactivityviews.AppInfoView
import com.mbp16.shsdishwiget.activity.settingsactivityviews.DatabaseManagementView
import com.mbp16.shsdishwiget.activity.settingsactivityviews.MainScreenSettingView
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme

class SettingsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHSDishWigetTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SettingView(this)
                }
            }
        }
    }
}

@Composable
fun SettingView(activity: Activity) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        MainScreenSettingView(activity)
        DatabaseManagementView(activity)
        AppInfoView(activity)
    }
}