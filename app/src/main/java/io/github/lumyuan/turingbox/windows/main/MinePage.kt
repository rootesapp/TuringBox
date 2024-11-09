package io.github.lumyuan.turingbox.windows.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

@Composable
fun MinePage() {
    // 外层 Column，填充整个屏幕
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Card 包裹每个按钮，按需调整
        Card(
            modifier = Modifier
                .fillMaxWidth()  // Card 填满宽度
                .padding(8.dp)   // Card 内部的内边距
        ) {
            // 按钮1
            Button(onClick = { 
                Toast.makeText(LocalContext.current, "你点了 OTG功能", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "OTG功能")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()  // Card 填满宽度
                .padding(8.dp)   // Card 内部的内边距
        ) {
            // 按钮2
            Button(onClick = { 
                Toast.makeText(LocalContext.current, "你点了 Magisk功能", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Magisk功能")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()  // Card 填满宽度
                .padding(8.dp)   // Card 内部的内边距
        ) {
            // 按钮3
            Button(onClick = { 
                Toast.makeText(LocalContext.current, "你点了 Lsposed/Xpoesd", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Lsposed/Xpoesd")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()  // Card 填满宽度
                .padding(8.dp)   // Card 内部的内边距
        ) {
            // 按钮4
            Button(onClick = { 
                Toast.makeText(LocalContext.current, "你点了 系统功能", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "系统功能")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()  // Card 填满宽度
                .padding(8.dp)   // Card 内部的内边距
        ) {
            // 按钮5
            Button(onClick = { 
                Toast.makeText(LocalContext.current, "你点了 界面功能", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "界面功能")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()  // Card 填满宽度
                .padding(8.dp)   // Card 内部的内边距
        ) {
            // 按钮6
            Button(onClick = { 
                Toast.makeText(LocalContext.current, "你点了 文件功能", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "文件功能")
            }
        }
    }
}
