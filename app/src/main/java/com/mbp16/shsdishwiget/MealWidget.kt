package com.mbp16.shsdishwiget

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.Button
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import java.util.Calendar

class MealWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val todayMeal = remember { mutableStateListOf("Loading", "Loading", "Loading") }
        val today = remember { mutableStateListOf(0, 0, 0) }
        fun updateInfo() {
            todayMeal.clear()
            todayMeal.addAll(arrayListOf("Loading", "Loading", "Loading"))
            val calendar = Calendar.getInstance()
            today.clear()
            today.addAll(
                arrayListOf(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            )
            var mealType = 0
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 13) mealType = 1
            Thread {
                val data = GetMealSignleWidget(today[0], today[1], today[2], mealType)
                todayMeal.clear()
                todayMeal.addAll(data)
            }.start()
        }
        LaunchedEffect(Unit) {
            updateInfo()
        }

        Column (
            modifier = GlanceModifier.padding(8.dp).fillMaxSize().background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity<MainActivity>())
        )
        {
            Text(
                text = "${today[0]}년 ${today[1]}월 ${today[2]}일",
                style= TextStyle(fontSize = 28.sp, textAlign = TextAlign.Center, color = GlanceTheme.colors.onSurface),
                modifier = GlanceModifier.padding(8.dp).fillMaxWidth()
            )
            Text(
                text = todayMeal[0],
                style = TextStyle(color = GlanceTheme.colors.error, fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.padding(8.dp, 8.dp, 8.dp, 12.dp)
            )
            for (i in todayMeal[1].split(",")) {
                Text(
                    text = i,
                    style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 18.sp),
                    modifier = GlanceModifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Text(text = todayMeal[2],
                style = TextStyle(color = GlanceTheme.colors.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold),
                 modifier = GlanceModifier.padding(8.dp, 12.dp, 8.dp, 8.dp)
            )
        }
        Box(
            modifier = GlanceModifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Button(
                text = "↺",
                onClick = {
                    updateInfo()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GlanceTheme.colors.surface,
                    contentColor = GlanceTheme.colors.error
                ),
                modifier = GlanceModifier.padding(8.dp).width(50.dp).height(50.dp)
            )
        }
    }
}

class MealWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MealWidget()
}