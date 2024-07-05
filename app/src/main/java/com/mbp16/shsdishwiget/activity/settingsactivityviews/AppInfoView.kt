package com.mbp16.shsdishwiget.activity.settingsactivityviews

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.preferences.core.edit
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.mbp16.shsdishwiget.R
import com.mbp16.shsdishwiget.activity.mainactivityviews.UpdateView
import com.mbp16.shsdishwiget.activity.settingsactivityviews.MainActivitySettingDataStore.Companion.dataStore
import com.mbp16.shsdishwiget.utils.Release
import com.mbp16.shsdishwiget.utils.getUpdate
import kotlinx.coroutines.launch

@Composable
fun AppInfoView(activity: Activity) {
    val dataStore = (LocalContext.current).dataStore
    val coroutineScope = rememberCoroutineScope()

    val updateAuto = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        dataStore.data.collect() { preferences ->
            updateAuto.value = preferences[MainActivitySettingDataStore.updateAuto] ?: true
        }
    }

    val dialogViewing = remember { mutableStateOf(false) }
    val result = remember { mutableStateOf(Release(tag_name = "", listOf(), "")) }

    fun checkUpdate() {
        val thread = Thread() {
            val requestResult = getUpdate(activity)
            if (requestResult != false) {
                result.value = requestResult as Release
                dialogViewing.value = true
            } else {
                object : Thread() {
                    override fun run() {
                        Looper.prepare()
                        Toast.makeText(activity, "업데이트가 없습니다", Toast.LENGTH_SHORT).show()
                        Looper.loop()
                    }
                }.start()
            }
        }
        thread.setUncaughtExceptionHandler { _, _ -> dialogViewing.value = false}
        thread.start()
    }

    if (dialogViewing.value) {
        UpdateView(dialogViewing = dialogViewing, result = result.value, activity = activity)
    }

    Text(text = "앱 정보", modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
        fontSize = MaterialTheme.typography.displaySmall.fontSize)
    Text(
        text="오픈소스 라이선스",
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(64.dp)
            .padding(20.dp)
            .clickable {
                Intent(activity, OssLicensesMenuActivity::class.java).also {
                    startActivity(
                        activity,
                        it,
                        null
                    )
                }
            },
        fontSize = MaterialTheme.typography.titleMedium.fontSize
    )
    Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(64.dp)
            .padding(20.dp)
            .clickable { checkUpdate() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "업데이트 자동 확인", fontSize = MaterialTheme.typography.titleMedium.fontSize)
        Switch(checked = updateAuto.value, onCheckedChange = {
            updateAuto.value = it
            coroutineScope.launch {
                dataStore.edit { preferences ->
                    preferences[MainActivitySettingDataStore.updateAuto] = it
                }
            }
        })
    }
    Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(64.dp)
            .padding(20.dp)
            .clickable { checkUpdate() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "버전", fontSize = MaterialTheme.typography.titleMedium.fontSize)
        Text(text = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Image(painter = painterResource(id = R.drawable.github), contentDescription = "GITHUB",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    activity.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/MBP16/SHSDishWidget")
                        )
                    )
                })
        Image(painter = painterResource(id = R.drawable.discord), contentDescription = "DISCORD",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .size(64.dp)
                .padding(start = 16.dp)
                .clickable {
                    activity.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://discord.com/users/783147071808471090")
                        )
                    )
                })
    }
}