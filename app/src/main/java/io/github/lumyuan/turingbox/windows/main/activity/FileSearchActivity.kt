package io.github.lumyuan.turingbox.windows.main.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.Column
import android.os.Environment
import android.widget.Toast
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
    var fileList by remember { mutableStateOf(emptyList<File>()) }
    var fileCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var showDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<File?>(null) }
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
            // Android 13及以上版本
            requestMultiplePermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 和 Android 12
            if (!Environment.isExternalStorageManager()) {
                showPermissionDialog = true
            }
        } else {
            // Android 10及以下版本
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // 显示权限请求对话框
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { /* 关闭对话框时的操作 */ },
            title = { Text("权限请求") },
            text = { Text("应用需要访问您的存储权限，请允许权限以继续") },
            confirmButton = {
                Button(
                    onClick = {
                        // 启动权限请求
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        showPermissionDialog = false
                    }
                ) {
                    Text("允许")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("取消")
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

    // 搜索文件的逻辑
    fun searchFiles(type: String): List<File> {
        val directory = Environment.getExternalStorageDirectory()
        return directory.walkTopDown().filter { file ->
            // 添加文件类型过滤逻辑
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

    // 文件删除操作
    fun deleteFile(file: File) {
        file.delete()
        Toast.makeText(context, "${file.name} 已删除", Toast.LENGTH_SHORT).show()
    }

    // UI部分
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // 设置背景颜色为灰色
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
                            fileToDelete?.let { deleteFile(it) }
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
