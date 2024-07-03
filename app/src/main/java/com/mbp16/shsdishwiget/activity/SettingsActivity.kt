package com.mbp16.shsdishwiget.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mbp16.shsdishwiget.activity.settingsactivityviews.AppInfoView
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
        AppInfoView(activity)
    }
}