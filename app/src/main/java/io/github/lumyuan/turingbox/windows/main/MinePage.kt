package io.github.lumyuan.turingbox.windows.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

@Composable
fun MinePage() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { 
            Toast.makeText(LocalContext.current, "你点了 OTG功能", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "OTG功能")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { 
            Toast.makeText(LocalContext.current, "你点了 Magisk功能", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Magisk功能")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { 
            Toast.makeText(LocalContext.current, "你点了 Lsposed/Xpoesd", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Lsposed/Xpoesd")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { 
            Toast.makeText(LocalContext.current, "你点了 系统功能", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "系统功能")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { 
            Toast.makeText(LocalContext.current, "你点了 界面功能", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "界面功能")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { 
            Toast.makeText(LocalContext.current, "你点了 文件功能", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "文件功能")
        }
    }
}
