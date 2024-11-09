package io.github.lumyuan.turingbox.windows.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MinePage() {
    val context = LocalContext.current  // 使用 LocalContext 获取上下文

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ButtonCard("OTG功能") {
            Toast.makeText(context, "你点了 OTG功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonCard("Magisk功能") {
            Toast.makeText(context, "你点了 Magisk功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonCard("Lsposed/Xposed") {
            Toast.makeText(context, "你点了 Lsposed/Xposed", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonCard("系统功能") {
            Toast.makeText(context, "你点了 系统功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonCard("界面功能") {
            Toast.makeText(context, "你点了 界面功能", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(8.dp))

        ButtonCard("文件功能") {
            Toast.makeText(context, "你点了 文件功能", Toast.LENGTH_SHORT).show()
        }
    }
}

// 提取一个通用的 ButtonCard 组合函数
@Composable
fun ButtonCard(buttonText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(onClick = onClick) {
            Text(text = buttonText)
        }
    }
}
