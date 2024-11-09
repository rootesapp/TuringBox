package io.github.lumyuan.turingbox.windows.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MinePage() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CustomButton("OTG功能") {
            Toast.makeText(context, "你点了 OTG功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton("Magisk功能") {
            Toast.makeText(context, "你点了 Magisk功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton("Lsposed/Xposed") {
            Toast.makeText(context, "你点了 Lsposed/Xposed", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton("系统功能") {
            Toast.makeText(context, "你点了 系统功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton("界面功能") {
            Toast.makeText(context, "你点了 界面功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton("文件功能") {
            Toast.makeText(context, "你点了 文件功能", Toast.LENGTH_SHORT).show()
        }
    }
}

// 提取一个自定义的按钮
@Composable
fun CustomButton(buttonText: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // 调整按钮之间的间距
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(text = buttonText)
    }
}
