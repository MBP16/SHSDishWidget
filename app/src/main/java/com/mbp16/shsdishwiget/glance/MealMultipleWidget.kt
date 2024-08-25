package com.mbp16.shsdishwiget.glance

import android.content.Context
import android.graphics.Color.parseColor
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
import com.mbp16.shsdishwiget.utils.getMeals
import java.util.*

class MealMultipleWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent(context)
            }
        }
    }

    override val stateDefinition = PreferencesGlanceStateDefinition

    @Composable
    private fun WidgetContent(context: Context) {
        val mealDataStore = context.mealDataStore
        val settingsFirmed = remember { mutableStateOf(false) }

        val errorOcurred = remember { mutableStateOf(false) }

        val prefs = currentState<Preferences>()
        val showNextWeek = prefs[booleanPreferencesKey("showNextWeek")] ?: false
        val backgroundColor = prefs[stringPreferencesKey("backgroundColor")] ?: "ff171b1e"

        val todayWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val mealData = remember { mutableStateListOf<ArrayList<ArrayList<String>>>()}
        val week = remember { mutableStateListOf<ArrayList<Int>>() }

        fun setWeek() {
            errorOcurred.value = true
            week.clear()
            mealData.clear()
            for (i in 2..6) {
                val cal = Calendar.getInstance()
                if (showNextWeek) { if (i - todayWeekDay < 0) cal.add(Calendar.DATE, 7) }
                cal.add(Calendar.DATE, i - todayWeekDay)
                week.add(arrayListOf(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)))
                mealData.add(
                    arrayListOf(
                        arrayListOf("Loading", "Loading", "Loading"),
                        arrayListOf("Loading", "Loading", "Loading"),
                    )
                )
            }
        }
        fun updateData() {
            fun threadExceptionHandler() {
                mealData.clear()
                for (i in 2..6) {
                    mealData.add(
                        arrayListOf(
                            arrayListOf("Error", "Error", "Error"),
                            arrayListOf("Error", "Error", "Error"),
                        )
                    )
                }
            }
            val thread = Thread {
                Runnable {
                    val data = getMeals(ArrayList(week), context)
                    mealData.clear()
                    mealData.addAll(data)
                    errorOcurred.value = false
                }.run()
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
                setWeek()
                updateData()
            }
        }
        if (settingsFirmed.value) {
            Row(
                modifier = GlanceModifier.fillMaxSize()
                    .background(ColorProvider(Color(parseColor("#$backgroundColor"))))
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                for (i in 0..<week.size) {
                    MealCard(week[i], mealData[i], todayWeekDay - 2 == i)
                }
            }
            if (errorOcurred.value) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        text = "↺",
                        onClick = {
                            setWeek()
                            updateData()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorProvider(Color(parseColor("#$backgroundColor"))),
                            contentColor = ColorProvider(Color(parseColor("#ffe4bebd")))
                        ),
                        modifier = GlanceModifier.padding(8.dp).width(50.dp).height(50.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = GlanceModifier.padding(8.dp).fillMaxSize()
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
                        color = ColorProvider(Color(parseColor("#ffffffff"))),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.padding(8.dp).fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun RowScope.MealCard(day: ArrayList<Int>, dayMeal: ArrayList<ArrayList<String>>, isToday: Boolean) {
        val prefs = currentState<Preferences>()
        val margin = prefs[intPreferencesKey("margin")] ?: 8

        val dateFontSize = prefs[intPreferencesKey("dateFontSize")] ?: 28
        val titleFontSize = prefs[intPreferencesKey("titleFontSize")] ?: 20
        val mealFontSize = prefs[intPreferencesKey("mealFontSize")] ?: 18
        val calorieFontSize = prefs[intPreferencesKey("calorieFontSize")] ?: 20

        val dateColor = prefs[stringPreferencesKey("dateColor")] ?: "ffe2e3e5"
        val titleColor = prefs[stringPreferencesKey("titleColor")] ?: "ffe4bebd"
        val mealColor = prefs[stringPreferencesKey("mealColor")] ?: "ffe2e3e5"
        val calorieColor = prefs[stringPreferencesKey("calorieColor")] ?: "ff8dcae7"
        val todayColor = prefs[stringPreferencesKey("todayColor")] ?: "cc2df07b"

        Column (
            modifier = GlanceModifier.defaultWeight().fillMaxHeight().padding(margin.dp),
        ) {
            Text(
                text = "${day[0]}년 ${day[1]}월 ${day[2]}일",
                style= TextStyle(fontSize = dateFontSize.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color(parseColor(if (isToday) "#$todayColor" else "#$dateColor")))),
                modifier = GlanceModifier.padding(margin.dp).fillMaxWidth()
            )
            for (i in dayMeal) {
                Column (modifier = GlanceModifier.padding(margin.dp).fillMaxWidth().fillMaxHeight().defaultWeight()) {
                    Text(text = i[0], modifier = GlanceModifier.padding(margin.dp),
                        style = TextStyle(color = ColorProvider(Color(parseColor("#$titleColor"))), fontSize = titleFontSize.sp, fontWeight = FontWeight.Bold))
                    for (j in i[1].split("\n")) {
                        Text(text = j, style = TextStyle(color = ColorProvider(Color(parseColor("#$mealColor"))), fontSize = mealFontSize.sp, fontWeight = FontWeight.Bold),
                            modifier = GlanceModifier.padding(horizontal = margin.dp, vertical = (margin/4.0).dp))
                    }
                    Text(text = i[2],
                        style = TextStyle(color = ColorProvider(Color(parseColor("#$calorieColor"))), fontSize = calorieFontSize.sp, fontWeight = FontWeight.Bold),
                        modifier = GlanceModifier.padding(margin.dp))
                }
            }
        }
    }
}

class MealMultipleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MealMultipleWidget()
}
