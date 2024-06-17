package com.mbp16.shsdishwiget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mbp16.shsdishwiget.ui.theme.SHSDishWigetTheme
import com.mbp16.shsdishwiget.GetMealData
import com.mbp16.shsdishwiget.GetMealId
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SHSDishWigetTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GetMeal()
                }
            }
        }
    }
}

@Composable
fun GetMeal() {
    var mealData by remember { mutableStateOf<String>("No Data") }
    val date by remember { mutableStateOf(Calendar.getInstance()) }
    var mealType by remember { mutableIntStateOf(0) }
    fun GetData() {
        Thread(Runnable {
            val id = GetMealId(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH), mealType)
            if (id == "No Data") {
                mealData = "No Data"
            }
            mealData = GetMealData(id.toInt(), "식단") ?: "No Data"
        }).start()
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly) {
        Text(text = "${date.get(Calendar.YEAR)}년 ${date.get(Calendar.MONTH) + 1}월 ${date.get(Calendar.DAY_OF_MONTH)}일")
        Text(text=mealData.replace(",", "\n"))
        Button(onClick = {
            date.add(Calendar.DAY_OF_MONTH, 1)
            GetData()
        }) {
            Text(text = "+1")
        }
        Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            , onClick = {
            date.add(Calendar.DAY_OF_MONTH, -1)
            GetData()
        }) {
            Text(text = "-1")
        }
        Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            , onClick = {
                if (mealType == 0) {
                    mealType = 1
                } else {
                    mealType = 0
                }
                GetData()
            }) {
            Text(text = "Type")
        }
    }
}