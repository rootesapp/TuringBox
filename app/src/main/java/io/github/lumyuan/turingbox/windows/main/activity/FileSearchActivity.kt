package io.github.lumyuan.turingbox.windows.main.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class FileSearchActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestManageExternalStorageLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "管理存储权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "管理存储权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13及以上版本
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                // 权限已授予，初始化UI
                setContent {
                    FileSearchView()
                }
            } else {
                // 请求媒体文件权限
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 到 Android 12（API 29 到 API 32）
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                // 权限已授予，初始化UI
                setContent {
                    FileSearchView()
                }
            } else {
                // 请求读取存储权限
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            // 对于 Android 9（API 28）及以下，直接初始化UI
            setContent {
                FileSearchView()
            }
        }
    }
}

@Composable
fun FileSearchView() {
    var category by remember { mutableStateOf("所有文件") }
    var files by remember { mutableStateOf(getFiles()) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // TopAppBar with a Back button
        TopAppBar(
            title = { Text("文件搜索") },
            navigationIcon = {
                IconButton(onClick = { /* Handle back navigation */ }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 搜索框
        BasicTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                files = getFiles(category).filter { file -> file.name.contains(searchQuery, true) }
            },
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // 文件类别选择
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { category = "音乐"; files = getFiles(category) }) {
                Text("音乐")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { category = "视频"; files = getFiles(category) }) {
                Text("视频")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { category = "文档"; files = getFiles(category) }) {
                Text("文档")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { category = "安装包"; files = getFiles(category) }) {
                Text("安装包")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { category = "空目录"; files = getFiles(category) }) {
                Text("空目录")
            }
        }

        // 文件列表
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            items(files) { file ->
                FileItem(file = file)
            }
        }
    }
}

@Composable
fun FileItem(file: File) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(file.name, modifier = Modifier.weight(1f))

        // 操作按钮
        Row {
            IconButton(onClick = { openFile(file) }) {
                Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = "打开")
            }
            IconButton(onClick = { deleteFile(file) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }
}

// 获取文件列表并按类别分类
fun getFiles(category: String = "所有文件"): List<File> {
    val dir = File(Environment.getExternalStorageDirectory().path)
    val files = dir.listFiles() ?: arrayOf()

    return when (category) {
        "音乐" -> files.filter { it.extension in listOf("mp3", "wav", "flac") }
        "视频" -> files.filter { it.extension in listOf("mp4", "avi", "mkv", "mov") }
        "文档" -> files.filter { it.extension in listOf("pdf", "doc", "docx", "ppt") }
        "安装包" -> files.filter { it.extension == "apk" }
        "空目录" -> files.filter { it.isDirectory && it.list().isEmpty() }
        else -> files.toList()
    }
}

// 打开文件
fun openFile(file: File) {
    val uri = Uri.fromFile(file)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

// 删除文件
fun deleteFile(file: File): Boolean {
    return if (file.exists()) {
        file.delete()
    } else {
        false
    }
}
