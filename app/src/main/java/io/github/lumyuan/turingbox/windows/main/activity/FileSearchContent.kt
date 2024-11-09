package io.github.lumyuan.turingbox.windows.main.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class FileSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FileSearchContent()
        }
    }
}

@Composable
fun FileSearchContent() {
    val context = LocalContext.current
    var fileType by remember { mutableStateOf("所有文件") }
    var fileList by remember { mutableStateOf(emptyList<File>()) }
    var showDialog by remember { mutableStateOf(false) }

    // 请求权限
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "权限已获取", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "未获取权限", Toast.LENGTH_SHORT).show()
        }
    }

    // 在初始化时检查文件访问权限
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showDialog = true
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "文件搜索",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { fileList = searchFiles(fileType) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "搜索 $fileType")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(fileList.size) { index ->
                val file = fileList[index]
                Text(
                    text = file.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            if (file.exists()) {
                                file.delete()
                                fileList = fileList - file
                                Toast.makeText(context, "${file.name} 已删除", Toast.LENGTH_SHORT).show()
                            }
                        }
                )
                Divider()
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("需要文件访问权限") },
                text = { Text("请允许应用访问所有文件以进行搜索") },
                confirmButton = {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    data = Uri.parse("package:" + context.packageName)
                                }
                                context.startActivity(intent)
                            }
                            showDialog = false
                        }
                    ) {
                        Text("前往设置")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

fun searchFiles(type: String): List<File> {
    val directory = Environment.getExternalStorageDirectory()
    return directory.walkTopDown().filter { file ->
        when (type) {
            "音乐" -> file.extension in listOf("mp3", "wav", "flac")
            "视频" -> file.extension in listOf("mp4", "mkv", "avi")
            "文档" -> file.extension in listOf("txt", "pdf", "docx")
            "空文件" -> file.length() == 0L
            else -> true // 搜索所有文件
        }
    }.toList()
}
