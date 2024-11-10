package io.github.lumyuan.turingbox.windows.main.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import java.io.File
import android.os.Environment
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.rememberLauncherForActivityResult

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
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "权限已获取", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "未获取权限", Toast.LENGTH_SHORT).show()
        }
    }

    // 请求多个权限
    val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.READ_MEDIA_IMAGES] == true &&
            permissions[Manifest.permission.READ_MEDIA_VIDEO] == true &&
            permissions[Manifest.permission.READ_MEDIA_AUDIO] == true) {
            Toast.makeText(context, "所有权限已授予", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "部分权限未授予", Toast.LENGTH_SHORT).show()
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
            // 文件过滤逻辑
            true // 搜索所有文件
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

    // UI部分
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 设置背景颜色为白色
    ) {
        // 顶部 App Bar
        TopAppBar(
            title = { Text("文件搜索") },
            navigationIcon = {
                IconButton(onClick = { /* 返回功能*/ }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                }
            },
            backgroundColor = Color.Blue,
            contentColor = Color.White
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

        Divider() // 分割线

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
}

@Composable
fun SettingsDialogSectionTitle(text: String) {
    Text(
        text = text,
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
        modifier = Modifier.padding(16.dp)
    )
}
