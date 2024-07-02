package com.mbp16.shsdishwiget.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
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
import com.mbp16.shsdishwiget.activity.MainActivity
import com.mbp16.shsdishwiget.utils.GetMealSignleWidget
import java.util.*

class MealWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<Preferences>()
        val margin = prefs[intPreferencesKey("margin")] ?: 8
        val dateFontSize = prefs[intPreferencesKey("dateFontSize")] ?: 28
        val titleFontSize = prefs[intPreferencesKey("titleFontSize")] ?: 20
        val mealFontSize = prefs[intPreferencesKey("mealFontSize")] ?: 18
        val calorieFontSize = prefs[intPreferencesKey("calorieFontSize")] ?: 20

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
            modifier = GlanceModifier.padding(margin.dp).fillMaxSize().background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity<MainActivity>())
        )
        {
            Text(
                text = "${today[0]}년 ${today[1]}월 ${today[2]}일",
                style= TextStyle(fontSize = dateFontSize.sp, textAlign = TextAlign.Center, color = GlanceTheme.colors.onSurface, fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.padding(margin.dp).fillMaxWidth()
            )
            Text(
                text = todayMeal[0],
                style = TextStyle(color = GlanceTheme.colors.error, fontSize = titleFontSize.sp, fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.padding(margin.dp)
            )
            for (i in todayMeal[1].split(",")) {
                Text(
                    text = i,
                    style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = mealFontSize.sp, fontWeight = FontWeight.Bold),
                    modifier = GlanceModifier.padding(horizontal = margin.dp, vertical = (margin/4.0).dp)
                )
            }
            Text(text = todayMeal[2],
                style = TextStyle(color = GlanceTheme.colors.primary, fontSize = calorieFontSize.sp, fontWeight = FontWeight.Bold),
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
