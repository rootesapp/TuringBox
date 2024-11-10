package io.github.lumyuan.turingbox.windows.main.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import io.github.lumyuan.turingbox.windows.main.viewmodel.FileSearchViewModel
import java.io.File

class FileSearchActivity : ComponentActivity() {

    private val fileSearchViewModel: FileSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileSearchScreen(fileSearchViewModel)
        }
    }

    @Composable
    fun FileSearchScreen(fileSearchViewModel: FileSearchViewModel) {
        val fileList = fileSearchViewModel.getFileList()

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("文件搜索") })
            }
        ) { paddingValues ->
            Row(modifier = Modifier.padding(paddingValues)) {
                FileCategoriesList(
                    modifier = Modifier.weight(0.3f),
                    onCategorySelected = { category -> 
                        fileSearchViewModel.filterFilesByCategory(category)
                    }
                )
                FileList(
                    modifier = Modifier.weight(0.7f),
                    fileList = fileList,
                    onFileClicked = { file -> 
                        showFileActionsDialog(file)
                    }
                )
            }
        }
    }

    @Composable
    fun FileCategoriesList(modifier: Modifier, onCategorySelected: (String) -> Unit) {
        val categories = listOf("音乐", "视频", "文档", "安装包", "空目录")
        
        LazyColumn(modifier = modifier) {
            items(categories) { category ->
                TextButton(
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(text = category)
                }
            }
        }
    }

    @Composable
    fun FileList(modifier: Modifier, fileList: List<File>, onFileClicked: (File) -> Unit) {
        LazyColumn(modifier = modifier) {
            items(fileList) { file ->
                FileItem(file = file, onClick = { onFileClicked(file) })
            }
        }
    }

    @Composable
    fun FileItem(file: File, onClick: () -> Unit) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = file.name)
        }
    }

    private fun showFileActionsDialog(file: File) {
        val actions = listOf("删除", "打开")
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("选择操作")
            .setItems(actions.toTypedArray()) { _, which ->
                when (which) {
                    0 -> deleteFile(file)
                    1 -> openFile(file)
                }
            }
            .show()
    }

    private fun deleteFile(file: File) {
        if (file.exists() && file.delete()) {
            Toast.makeText(this, "文件已删除", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeType(file))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension
        return when {
            extension.equals("mp3", true) -> "audio/mp3"
            extension.equals("mp4", true) -> "video/mp4"
            extension.equals("pdf", true) -> "application/pdf"
            extension.equals("apk", true) -> "application/vnd.android.package-archive"
            else -> "*/*"
        }
    }
}
