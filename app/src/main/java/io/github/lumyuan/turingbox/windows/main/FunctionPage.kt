package io.github.lumyuan.turingbox.windows.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun FunctionPage() {
    val context = LocalContext.current

    // 创建滚动状态
    val scrollState = rememberScrollState()

    // 使用 Column 并包裹在 verticalScroll 中
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState) // 使 Column 可垂直滚动
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

// 自定义按钮，应用 MaterialTheme 配色方案
@Composable
fun CustomButton(buttonText: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()  // 按钮宽度填满父容器
            .padding(vertical = 8.dp), // 调整按钮的上下间距
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(text = buttonText)
    }
}
