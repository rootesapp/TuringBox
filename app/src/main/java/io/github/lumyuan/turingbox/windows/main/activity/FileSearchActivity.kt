package io.github.lumyuan.turingbox.windows.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
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
    }
}

@Composable
fun FileSearchView() {
    var category by remember { mutableStateOf("所有文件") }
    var files by remember { mutableStateOf(getFiles()) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        // TopAppBar with a Back button
        TopAppBar(
            title = { Text("文件搜索") },
            navigationIcon = {
                IconButton(onClick = { /* Handle back navigation */ }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
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
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(file.name, modifier = Modifier.weight(1f))

        // 操作按钮
        Row {
            IconButton(onClick = { openFile(context, file) }) {
                Icon(imageVector = Icons.Filled.OpenInBrowser, contentDescription = "打开")
            }
            IconButton(onClick = { deleteFile(file) }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "删除")
            }
        }
    }
}

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

fun openFile(context: Context, file: File) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.fromFile(file), "application/*")
    }
    context.startActivity(intent)
}

fun deleteFile(file: File) {
    if (file.delete()) {
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FileSearchView()
}
