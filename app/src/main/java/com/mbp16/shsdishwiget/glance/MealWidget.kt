package com.mbp16.shsdishwiget.glance

import android.content.Context
import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mbp16.shsdishwiget.activity.MainActivity
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore.Companion.mealDataStore
import com.mbp16.shsdishwiget.utils.getMealSignleWidget
import java.util.*

class MealWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent(context)
            }
        }
    }

    @Composable
    private fun WidgetContent(context: Context) {
        val mealDataStore = context.mealDataStore
        val settingsFirmed = remember { mutableStateOf(false) }

        val prefs = currentState<Preferences>()

        val changeLunch = prefs[intPreferencesKey("changeLunch")] ?: 1800
        val changeDinner = prefs[intPreferencesKey("changeDinner")] ?: 1300

        val margin = prefs[intPreferencesKey("margin")] ?: 8

        val dateFontSize = prefs[intPreferencesKey("dateFontSize")] ?: 28
        val titleFontSize = prefs[intPreferencesKey("titleFontSize")] ?: 20
        val mealFontSize = prefs[intPreferencesKey("mealFontSize")] ?: 18
        val calorieFontSize = prefs[intPreferencesKey("calorieFontSize")] ?: 20

        val backgroundColor = prefs[stringPreferencesKey("backgroundColor")] ?: "ff171b1e"
        val dateColor = prefs[stringPreferencesKey("dateColor")] ?: "ffe2e3e5"
        val titleColor = prefs[stringPreferencesKey("titleColor")] ?: "ffe4bebd"
        val mealColor = prefs[stringPreferencesKey("mealColor")] ?: "ffe2e3e5"
        val calorieColor = prefs[stringPreferencesKey("calorieColor")] ?: "ff8dcae7"

        val todayMeal = remember { mutableStateListOf("Loading", "Loading", "Loading") }
        val today = remember { mutableStateListOf(0, 0, 0) }
        fun updateInfo() {
            todayMeal.clear()
            todayMeal.addAll(arrayListOf("Loading", "Loading", "Loading"))
            val calendar = Calendar.getInstance()
            val currentTime = calendar.get(Calendar.HOUR_OF_DAY)*100+calendar.get(Calendar.MINUTE)
            val mealType: Int
            if (currentTime in changeDinner..<changeLunch) { mealType = 3 }
            else if (currentTime >= changeLunch) {
                mealType = 2
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            } else { mealType = 2 }
            today.clear()
            today.addAll(
                arrayListOf(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            fun threadExceptionHandler() {
                todayMeal.clear()
                todayMeal.addAll(arrayListOf("Error", "Error", "Error"))
            }
            val thread = Thread {
                val data = getMealSignleWidget(today[0], today[1], today[2], mealType, context)
                todayMeal.clear()
                todayMeal.addAll(data)
            }
            thread.setUncaughtExceptionHandler { _, _ -> threadExceptionHandler() }
            thread.start()
        }
        LaunchedEffect(Unit) {
            mealDataStore.data.collect { preferences ->
                settingsFirmed.value = if ((preferences[GetMealSettingDataStore.schoolIdLink] ?: "") != "" ||
                    (preferences[GetMealSettingDataStore.neisSchoolCode] ?: "") != "") true else false
            }
        }
        LaunchedEffect(settingsFirmed.value) {
            if (settingsFirmed.value) {
                updateInfo()
            }
        }

        if (settingsFirmed.value) {
            Column(
                modifier = GlanceModifier.padding(margin.dp).fillMaxSize()
                    .background(ColorProvider(Color(parseColor("#$backgroundColor"))))
                    .clickable(actionStartActivity<MainActivity>())
            )
            {
                Text(
                    text = "${today[0]}년 ${today[1]}월 ${today[2]}일",
                    style = TextStyle(
                        fontSize = dateFontSize.sp,
                        textAlign = TextAlign.Center,
                        color = ColorProvider(Color(parseColor("#$dateColor"))),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(margin.dp).fillMaxWidth()
                )
                Text(
                    text = todayMeal[0],
                    style = TextStyle(
                        color = ColorProvider(Color(parseColor("#$titleColor"))),
                        fontSize = titleFontSize.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(margin.dp)
                )
                for (i in todayMeal[1].split("\n")) {
                    Text(
                        text = i,
                        style = TextStyle(
                            color = ColorProvider(Color(parseColor("#$mealColor"))),
                            fontSize = mealFontSize.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.padding(
                            horizontal = margin.dp,
                            vertical = (margin / 4.0).dp
                        )
                    )
                }
                Text(
                    text = todayMeal[2],
                    style = TextStyle(
                        color = ColorProvider(Color(parseColor("#$calorieColor"))),
                        fontSize = calorieFontSize.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(margin.dp)
                )
            }
            Box(
                modifier = GlanceModifier.fillMaxSize().padding(margin.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    text = "↺",
                    onClick = {
                        updateInfo()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorProvider(Color(parseColor("#$backgroundColor"))),
                        contentColor = ColorProvider(Color(parseColor("#ffe4bebd")))
                    ),
                    modifier = GlanceModifier.padding(8.dp).width(50.dp).height(50.dp)
                )
            }
        } else {
            Column(
                modifier = GlanceModifier.padding(margin.dp).fillMaxSize()
                    .background(ColorProvider(Color(parseColor("#$backgroundColor"))))
                    .clickable(actionStartActivity<MainActivity>()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    text = "학교 정보를 설정해주세요",
                    style = TextStyle(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = ColorProvider(Color(parseColor("#$dateColor"))),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(margin.dp).fillMaxWidth()
                )
            }
        }
    }
}

class MealWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MealWidget()
}
