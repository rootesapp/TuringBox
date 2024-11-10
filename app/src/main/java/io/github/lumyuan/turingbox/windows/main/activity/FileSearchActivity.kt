package io.github.lumyuan.turingbox.windows.main.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
    var showPermissionDialog by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<File?>(null) }
    var fileCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var showLoadingDialog by remember { mutableStateOf(false) }

    // 动态请求权限
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "权限已获取", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "未获取权限", Toast.LENGTH_SHORT).show()
        }
    }

    // 动态请求多个权限
    val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            Toast.makeText(context, "所有权限已获取", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "未完全获取权限", Toast.LENGTH_SHORT).show()
        }
    }

    // 初始化时检查文件访问权限
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestMultiplePermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showPermissionDialog = true
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // 搜索文件
    fun searchFiles(type: String): List<File> {
        val directory = Environment.getExternalStorageDirectory()
        return directory.walkTopDown().filter { file ->
            when (type) {
                "音乐" -> file.extension in listOf("mp3", "wav", "flac")
                "视频" -> file.extension in listOf("mp4", "mkv", "avi")
                "安装包" -> file.extension in listOf("apk")
                "文档" -> file.extension in listOf("txt", "pdf", "docx")
                "QQ文件" -> file.path.contains("/tencent/QQfile_recv/")
                "微信文件" -> file.path.contains("/tencent/MicroMsg/")
                "空目录" -> file.isDirectory && file.listFiles().isNullOrEmpty()
                "空文件" -> file.length() == 0L
                else -> true // 搜索所有文件
            }
        }.toList()
    }

    // 计算文件数量
    fun countFiles(): Map<String, Int> {
        val categories = listOf("音乐", "视频", "安装包", "文档", "QQ文件", "微信文件", "空目录", "空文件")
        return categories.associateWith { category -> searchFiles(category).size }
    }

    // 搜索并展示文件
    fun searchAndShowFiles(type: String) {
        showLoadingDialog = true
        fileList = searchFiles(type)
        showLoadingDialog = false
    }

    // 文件删除确认对话框
    fun showDeleteDialog(file: File) {
        fileToDelete = file
        showDialog = true
    }

    // UI部分
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "文件搜索",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 文件分类按钮
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val categories = listOf("音乐", "视频", "安装包", "文档", "QQ文件", "微信文件", "空目录", "空文件")
            items(categories.size) { index ->
                val category = categories[index]
                val count = fileCounts[category] ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = category, modifier = Modifier.weight(1f))
                    Text(text = "$count 文件", modifier = Modifier.align(Alignment.CenterVertically))
                    IconButton(onClick = { searchAndShowFiles(category) }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                }
            }
        }

        // 正在加载对话框
        if (showLoadingDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("正在加载") },
                text = { Text("文件正在加载，请稍候...") },
                confirmButton = {
                    Button(onClick = { }) {
                        Text("关闭")
                    }
                }
            )
        }

        // 文件删除确认对话框
        if (showDialog && fileToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("确认删除文件") },
                text = { Text("确定要删除 ${fileToDelete!!.name} 吗？") },
                confirmButton = {
                    Button(
                        onClick = {
                            fileToDelete?.delete()
                            Toast.makeText(context, "${fileToDelete!!.name} 已删除", Toast.LENGTH_SHORT).show()
                            showDialog = false
                            fileToDelete = null
                        }
                    ) {
                        Text("删除")
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

    // 更新文件数量
    LaunchedEffect(Unit) {
        fileCounts = countFiles()
    }
}
