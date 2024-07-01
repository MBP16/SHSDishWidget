package com.mbp16.shsdishwiget.activity

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mbp16.shsdishwiget.activity.mainactivityviews.MealView
import com.mbp16.shsdishwiget.activity.mainactivityviews.NoInternetView
import com.mbp16.shsdishwiget.activity.mainactivityviews.UpdateView
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import com.mbp16.shsdishwiget.utils.Release
import com.mbp16.shsdishwiget.utils.checkUpdate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conMan = getSystemService(ConnectivityManager::class.java)
        val networkInfo = conMan.activeNetwork
        val caps = conMan.getNetworkCapabilities(networkInfo)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (caps == false || caps == null) {
            setContent {
                SHSDishWigetTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        NoInternetView(this)
                    }
                }
            }
            return
        } else {
            setContent {
                val dialogViewing = remember { mutableStateOf(false) }
                var result: Any = false
                LaunchedEffect(Unit) {
                    val thread = Thread() {
                        result = checkUpdate(this@MainActivity)
                        if (result != false) {dialogViewing.value = true}
                    }
                    thread.setUncaughtExceptionHandler { _, _ -> dialogViewing.value = false }
                    thread.start()
                }
                SHSDishWigetTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        if (dialogViewing.value) {
                            UpdateView(dialogViewing, result as Release, this)
                        }
                        MealView(this)
                    }
                }
            }
        }
    }
}
