package com.mbp16.shsdishwiget.activity.mainactivityviews

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mbp16.shsdishwiget.utils.Release

@Composable
fun UpdateView(dialogViewing: MutableState<Boolean>, result: Release, activity: Activity) {
    Dialog(
        onDismissRequest = { dialogViewing.value = false }
    ) {
        Card(
            modifier = Modifier.requiredHeight(300.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Info")
                    Text(text = result.tag_name, modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleLarge)
                }
                Text(text = result.body)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { dialogViewing.value = false }) {
                        Text("닫기", modifier = Modifier.padding(4.dp))
                    }
                    TextButton(
                        onClick = {
                            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result.assets[0].browser_download_url)))
                        },
                        colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("다운로드", modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}