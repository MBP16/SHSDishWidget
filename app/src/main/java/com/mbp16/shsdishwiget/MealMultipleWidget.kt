package com.mbp16.shsdishwiget

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import java.util.Calendar

class MealMultipleWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val todayWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val mealData = remember { mutableStateListOf<ArrayList<ArrayList<String>>>()}
        val week = remember { mutableStateListOf<ArrayList<Number>>() }
        fun setWeek() {
            week.clear()
            mealData.clear()
            for (i in 2..6) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DATE, i - todayWeekDay)
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH) + 1
                val year = cal.get(Calendar.YEAR)
                week.add(arrayListOf(year, month, day))
                mealData.add(
                    arrayListOf(
                        arrayListOf("Loading", "Loading", "Loading"),
                        arrayListOf("Loading", "Loading", "Loading"),
                    )
                )
            }
        }
        fun updateData() {
            Thread {
                Runnable {
                    val data = GetMealData(ArrayList(week))
                    mealData.clear()
                    mealData.addAll(data)
                }.run()
            }.start()
        }
        LaunchedEffect(Unit) {
            setWeek()
            updateData()
        }
        Row(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            for (i in 0..<week.size) {
                MealCard(week[i], mealData[i])
            }
        }
    }

    @Composable
    fun RowScope.MealCard(day: ArrayList<Number>, dayMeal: ArrayList<ArrayList<String>>) {
        Column (
            modifier = GlanceModifier.defaultWeight().fillMaxHeight().padding(8.dp),
        ) {
            Text(
                text = "${day[0]}년 ${day[1]}월 ${day[2]}일",
                style= TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, color = GlanceTheme.colors.onSurface),
                modifier = GlanceModifier.padding(8.dp, 8.dp, 8.dp, 10.dp).fillMaxWidth()
            )
            for (i in dayMeal) {
                Column (modifier = GlanceModifier.padding(8.dp).fillMaxWidth().fillMaxHeight().defaultWeight()) {
                    Text(text = i[0], modifier = GlanceModifier.padding(8.dp),
                        style = TextStyle(color = GlanceTheme.colors.error, fontSize = 20.sp, fontWeight = FontWeight.Bold))
                    for (j in i[1].split(",")) {
                        Text(text = j, style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 14.sp),
                            modifier = GlanceModifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                    Text(text = i[2],
                        style = TextStyle(color = GlanceTheme.colors.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = GlanceModifier.padding(8.dp))
                }
            }
        }
    }
}

class MealMultipleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MealMultipleWidget()
}