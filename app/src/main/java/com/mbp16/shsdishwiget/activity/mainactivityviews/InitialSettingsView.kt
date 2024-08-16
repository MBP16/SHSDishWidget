package com.mbp16.shsdishwiget.activity.mainactivityviews

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore

@Composable
fun InitialSettingsView(activity: Activity, dataStore: DataStore<Preferences>) {
    val pageNumber = remember { mutableIntStateOf(1) }

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
    LaunchedEffect(pageNumber.intValue) {
        println(pageNumber.intValue)
    }

    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = pageNumber.intValue,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(500, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> -fullWidth }
                        )
            } else {
                slideInHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing),
                    initialOffsetX = { fullWidth -> -fullWidth }
                ) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(500, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> fullWidth }
                        )
            }
        }
    ) {
        when (pageNumber.intValue) {
            1 -> {
                OriginTypeChoose(originType = originType)
            }
            2 -> {
                OriginTypeChoose(originType = originType)
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = {
                    pageNumber.intValue -= 1
                },
                enabled = when (pageNumber.intValue) {
                    1 -> false
                    else -> true
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .requiredHeight(60.dp),
                shape = RoundedCornerShape(25),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.background)
            ) {
                Text(text = "이전", style=MaterialTheme.typography.titleLarge)
            }
            Button(
                onClick = {
                    pageNumber.intValue += 1
                },
                enabled = when (pageNumber.intValue) {
                    1 -> originType.value != ""
                    else -> false
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .requiredHeight(60.dp),
                shape = RoundedCornerShape(25)
            ) {
                Text(text = "다음", style=MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
fun OriginTypeChoose(originType: MutableState<String>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "정보 제공처 선택")
        Button(
            onClick = { originType.value = "neis" }
        ) {
            Text(text = "NEIS")
        }
        Button(
            onClick = { originType.value = "school" }
        ) {
            Text(text = "학교")
        }
    }
}