package io.github.lumyuan.turingbox.windows.main

import androidx.compose.runtime.*
import androidx.compose.material3.*

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

@Composable
fun FunctionPage() {
    val context = LocalContext.current

    // 创建滚动状态
    val scrollState = rememberScrollState()

    // 假设我们用一个异步操作来获取公告内容
    val announcement = remember { mutableStateOf("加载中...") }

    // 模拟从远程读取公告内容
    LaunchedEffect(Unit) {
        announcement.value = fetchAnnouncement()
    }

    // 使用 Column 并包裹在 verticalScroll 中
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState) // 使 Column 可垂直滚动
    ) {
        // 顶部公告
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "公告", // 标记为公告
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = announcement.value, // 显示公告内容
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // 以下是功能按钮部分
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

@Composable
fun CustomButton(buttonText: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()  // 按钮宽度填满父容器
            .padding(vertical = 8.dp), // 调整按钮的上下间距
        colors = ButtonDefaults.buttonColors() // 使用默认颜色
    ) {
        Text(text = buttonText)
    }
}

// 使用 OkHttp 获取公告内容
suspend fun fetchAnnouncement(): String {
    val client = OkHttpClient()  // 创建 OkHttp 客户端
    val request = Request.Builder()
        .url("https://rootes.top/公告.txt")  // 设置公告文本的 URL
        .build()

    return try {
        // 异步请求，读取响应体
        withContext(Dispatchers.IO) {
            val response: Response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string() ?: "公告内容为空"
            } else {
                "公告加载失败: ${response.code}"
            }
        }
    } catch (e: Exception) {
        "公告加载失败，请稍后再试"
    }
}
