package io.github.lumyuan.turingbox.windows.main.activity

import android.content.Context
import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun FileSearchContent(
    files: List<String>,
    isLoading: Boolean,
    isPermissionDenied: Boolean,
    onRequestPermission: () -> Unit
) {
    if (isPermissionDenied) {
        NoPermissionContent(onRequestPermission)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Popup(alignment = Alignment.Center) {
                    LoadingPopup()
                }
            }

            if (!isLoading && files.isEmpty()) {
                Text(text = "没有找到文件", textAlign = TextAlign.Center)
            } else {
                FileList(files)
            }
        }
    }
}

@Composable
fun LoadingPopup() {
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .size(200.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "正在加载...", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun NoPermissionContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "没有文件访问权限")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRequestPermission) {
            Text("请求权限")
        }
    }
}

@Composable
fun FileList(files: List<String>) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        files.forEach { file ->
            Text(text = file)
        }
    }
}

suspend fun searchFiles(context: Context): List<String> = withContext(Dispatchers.IO) {
    val musicExtensions = listOf(".mp3", ".wav", ".m4a")
    val videoExtensions = listOf(".mp4", ".avi", ".mkv")
    val docExtensions = listOf(".pdf", ".doc", ".docx", ".txt")

    val filesList = mutableListOf<String>()
    val root = Environment.getExternalStorageDirectory()

    fun search(directory: File) {
        directory.listFiles()?.forEach { file ->
            when {
                file.isDirectory -> search(file)
                file.extension in musicExtensions -> filesList.add("音乐文件: ${file.name}")
                file.extension in videoExtensions -> filesList.add("视频文件: ${file.name}")
                file.extension in docExtensions -> filesList.add("文档文件: ${file.name}")
                file.length() == 0L -> filesList.add("空文件: ${file.name}")
            }
        }
    }

    search(root)
    return@withContext filesList
}
