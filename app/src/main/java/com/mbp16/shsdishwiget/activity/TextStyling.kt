package com.mbp16.shsdishwiget.activity

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TextStyleChange(name: String, fontSizeVariable: MutableIntState) {
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
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = fontSizeVariable.intValue.toString(),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.requiredWidth(48.dp)
            )
        }
    }
}
