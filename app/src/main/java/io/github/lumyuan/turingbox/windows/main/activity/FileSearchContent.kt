package io.github.lumyuan.turingbox.windows.main.activity

import android.os.Bundle 
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import java.io.File
import kotlinx.coroutines.*

@OptIn(ExperimentalAnimationApi::class)
class FileSearchContent : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // 未授权时，跳转到设置页面
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 请求文件管理权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
        
        setContent {
            FileSearchApp()
        }
    }
}

@Composable
fun FileSearchApp() {
    var isLoading by remember { mutableStateOf(false) }
    var fileStats by remember { mutableStateOf<FileStats?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showNoPermissionDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (Environment.isExternalStorageManager()) {
            Button(onClick = {
                isLoading = true
                fileStats = null
                CoroutineScope(Dispatchers.IO).launch {
                    fileStats = searchFiles(File(Environment.getExternalStorageDirectory().toString()))
                    isLoading = false
                }
            }) {
                Text("搜索文件")
            }
        } else {
            showNoPermissionDialog = true
        }

        if (isLoading) {
            LoadingPopup()
        }

        fileStats?.let {
            Text("音乐文件: ${it.musicCount}")
            Text("视频文件: ${it.videoCount}")
            Text("文档文件: ${it.documentCount}")
            Text("空文件: ${it.emptyFileCount}")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showDeleteDialog = true }) {
                Text("删除所有空文件和目录")
            }
        }

        if (showDeleteDialog) {
            ConfirmDeletePopup(
                onDeleteConfirmed = {
                    CoroutineScope(Dispatchers.IO).launch {
                        fileStats?.let { stats ->
                            deleteFiles(stats.emptyFiles)
                            deleteDirectories(stats.emptyDirs)
                        }
                        fileStats = null
                        showDeleteDialog = false
                    }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        if (showNoPermissionDialog) {
            NoPermissionPopup { showNoPermissionDialog = false }
        }
    }
}

@Composable
fun LoadingPopup() {
    Popup(alignment = Alignment.Center) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "加载中...", style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    }
}

@Composable
fun ConfirmDeletePopup(onDeleteConfirmed: () -> Unit, onDismiss: () -> Unit) {
    Popup(alignment = Alignment.Center) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "确认删除", style = MaterialTheme.typography.h6)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("是否删除所有空文件和空目录？")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = onDismiss) {
                            Text("取消")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onDeleteConfirmed, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("删除", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoPermissionPopup(onDismiss: () -> Unit) {
    Popup(alignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("没有权限")
                Spacer(modifier = Modifier.height(8.dp))
                Text("请在设置中授予文件访问权限")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("确定")
                }
            }
        }
    }
}

// 文件搜索和删除逻辑
data class FileStats(
    val musicCount: Int,
    val videoCount: Int,
    val documentCount: Int,
    val emptyFileCount: Int,
    val emptyFiles: List<File>,
    val emptyDirs: List<File>
)

suspend fun searchFiles(root: File): FileStats {
    val musicExts = listOf("mp3", "wav", "aac")
    val videoExts = listOf("mp4", "mkv", "avi")
    val docExts = listOf("txt", "pdf", "docx")
    var musicCount = 0
    var videoCount = 0
    var documentCount = 0
    val emptyFiles = mutableListOf<File>()
    val emptyDirs = mutableListOf<File>()

    root.walk().forEach { file ->
        if (file.isFile) {
            when (file.extension.lowercase()) {
                in musicExts -> musicCount++
                in videoExts -> videoCount++
                in docExts -> documentCount++
            }
            if (file.length() == 0L) emptyFiles.add(file)
        } else if (file.isDirectory && file.listFiles()?.isEmpty() == true) {
            emptyDirs.add(file)
        }
    }

    return FileStats(musicCount, videoCount, documentCount, emptyFiles.size, emptyFiles, emptyDirs)
}

fun deleteFiles(files: List<File>) {
    files.forEach { it.delete() }
}

fun deleteDirectories(directories: List<File>) {
    directories.forEach { it.deleteRecursively() }
}
