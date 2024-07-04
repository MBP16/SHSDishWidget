package com.mbp16.shsdishwiget.activity

import android.graphics.Color.parseColor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.*

@Composable
fun TextStyleChange(name: String, fontSizeVariable: MutableIntState, colorVariable: MutableState<String>) {
    Column {
        Text(
            text=name,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text="크기",
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Slider(
                value = fontSizeVariable.intValue.toFloat(),
                onValueChange = {
                    fontSizeVariable.intValue = it.toInt()
                },
                valueRange = 0.0F..32.0F,
                steps = 33,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            )
            Text(
                text = fontSizeVariable.intValue.toString(),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                textAlign = TextAlign.Center
            )
        }
        ColorChangingRow(colorVariable)
    }
}

@Composable
fun ColorChangingRow(colorVariable: MutableState<String>) {
    val changingColor = remember { mutableStateOf(false) }

    if (changingColor.value) {
        ColorPickerDialog(changingColor = changingColor, colorVariable = colorVariable)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp, 0.dp, 16.dp, 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "색상",
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
        AlphaTile(
            selectedColor = Color(parseColor("#" + colorVariable.value)),
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(40.dp)).clickable { changingColor.value = true },
        )
    }
}

@Composable
fun ColorPickerDialog(changingColor: MutableState<Boolean>, colorVariable: MutableState<String>) {
    val controller = rememberColorPickerController()
    val currentColor = remember { mutableStateOf("ffffffff") }

    LaunchedEffect(Unit) {
        controller.selectByColor(Color(parseColor("#" + colorVariable.value)), false)
    }

    Dialog(
        onDismissRequest = { changingColor.value = false },
    ) {
        Card (
            modifier = Modifier.requiredHeight(630.dp).requiredWidth(350.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "색상 선택기",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                )
                HsvColorPicker(
                    modifier = Modifier.fillMaxWidth().height(300.dp).padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope ->
                        currentColor.value = colorEnvelope.hexCode
                    }
                )
                AlphaSlider(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).height(35.dp),
                    controller = controller
                )
                BrightnessSlider(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).height(35.dp),
                    controller = controller
                )
                Text(
                    text = colorVariable.value,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
                AlphaTile(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { changingColor.value = false }) {
                        Text("닫기", modifier = Modifier.padding(4.dp))
                    }
                    TextButton(
                        onClick = {
                            changingColor.value = false
                            colorVariable.value = currentColor.value
                        },
                        colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "확인",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
