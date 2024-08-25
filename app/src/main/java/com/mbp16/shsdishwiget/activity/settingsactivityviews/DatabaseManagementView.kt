package com.mbp16.shsdishwiget.activity.settingsactivityviews

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.room.Room
import com.mbp16.shsdishwiget.utils.MealDatabase

@Composable
fun DatabaseManagementView(activity: Activity) {
    val db = Room.databaseBuilder(activity, MealDatabase::class.java, "mealData").allowMainThreadQueries().build()
    val mealDataDao = db.mealDataDao()
    val keys = remember { mutableListOf<String>() }
    val storedDates = remember { mutableListOf<String>() }
    val viewingDialog = remember { mutableStateOf(false) }
    val viewingConfirmDialog = remember { mutableStateOf(false) }
    val totalCount = remember { mutableIntStateOf(0) }
    val fileSize = remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        totalCount.intValue = mealDataDao.getCount() / 10
        val data = mealDataDao.getAll()
        keys.clear()
        keys.addAll(data.map { it.dateType })
        storedDates.clear()
        for (i in data.indices step 10) {
            storedDates.add(data[i].dateType.dropLast(2) + "-" + data[i+9].dateType.dropLast(2))
        }
        fileSize.longValue = activity.getDatabasePath("mealData").length() / 1024
    }

    fun deleteData(index: Int) {
        val key = keys.subList(index * 10, index * 10 + 10)
        mealDataDao.delete(key)
        totalCount.intValue--
        keys.removeAll(key)
        storedDates.removeAt(index)
    }

    if (viewingDialog.value) { ManagingDialog(viewingDialog, { deleteData(it) }, storedDates) }

    if (viewingConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { viewingConfirmDialog.value = false },
            dismissButton = {
                Button(
                    onClick = { viewingConfirmDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "취소")
                }
            },
            confirmButton = {
                Button(onClick = {
                    mealDataDao.deleteAll()
                    totalCount.intValue = 0
                    storedDates.clear()
                    viewingConfirmDialog.value = false
                    Toast.makeText(activity, "데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "확인")
                }
            },
            title = { Text(text = "데이터 전체 삭제") },
            text = { Text(text = "정말로 데이터를 전체 삭제하시겠습니까?") }
        )
    }

    Text(text="저장된 데이터 설정", modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
        fontSize = MaterialTheme.typography.displaySmall.fontSize)
    Column {
        Row(
            modifier = Modifier
                .clickable { viewingDialog.value = true }
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("데이터 관리")
            Text("${fileSize.longValue}kb 총 ${totalCount.intValue}주 >")
        }
        HorizontalDivider()
        Text(text = "데이터 전체 삭제", modifier = Modifier
            .clickable { viewingConfirmDialog.value = true }
            .fillMaxWidth()
            .padding(20.dp))
    }
}

@Composable
fun ManagingDialog(viewingDialog: MutableState<Boolean>, deleteData: (Int) -> Unit, storedDateTypes: List<String>) {
    Dialog(onDismissRequest = { viewingDialog.value = false }, DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            LazyColumn() {
                item {
                    Text(text = "저장된 데이터", modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                        fontSize = MaterialTheme.typography.displaySmall.fontSize)
                }
                itemsIndexed(storedDateTypes) { index, dateType  ->
                    val displayName: List<String> = dateType.split("-")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 5.dp, 5.dp, 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${displayName[0]}년 ${displayName[1]}월 ${displayName[2]}일 ~ ${displayName[3]}년 ${displayName[4]}월 ${displayName[5]}일")
                        IconButton(onClick = { deleteData(index) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "삭제")
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { viewingDialog.value = false }) {
                            Text("닫기")
                        }
                    }
                }
            }
        }
    }
}
