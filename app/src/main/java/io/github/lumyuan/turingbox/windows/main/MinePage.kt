package io.github.lumyuan.turingbox.windows.main

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.lumyuan.turingbox.common.shell.KeepShellPublic
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun MinePage() {
    val context = LocalContext.current
    val privateScriptPath = File(context.filesDir, "powercfg.sh") // 软件私有目录中的脚本路径
    var scriptExists by remember { mutableStateOf(privateScriptPath.exists()) }
    val modeState = remember { mutableStateOf("调度已激活") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            copyScriptToPrivateDir(context, it, privateScriptPath)
            // 在文件导入后，修改权限为 777
            KeepShellPublic.doCmdSync("chmod 777 ${privateScriptPath.absolutePath}")
            scriptExists = privateScriptPath.exists() // 刷新界面状态
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (scriptExists) {
            Text(
                text = modeState.value,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PowerModeButton("省电", "powersave", modeState, privateScriptPath)
            PowerModeButton("均衡", "balance", modeState, privateScriptPath)
            PowerModeButton("性能", "performance", modeState, privateScriptPath)
            PowerModeButton("极速", "fast", modeState, privateScriptPath)
            PowerModeButton("自动", "auto", modeState, privateScriptPath)
        } else {
            // 显示“点击下载”按钮
            Button(
                onClick = {
                    Toast.makeText(context, "功能开发中", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "点击下载")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 显示“导入脚本”按钮，打开文件选择器
            Button(
                onClick = { launcher.launch("application/x-sh") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "导入脚本")
            }
        }
    }
}

@Composable
fun PowerModeButton(modeName: String, modeCommand: String, modeState: MutableState<String>, privateScriptPath: File) {
    val context = LocalContext.current
    Button(
        onClick = {
            KeepShellPublic.doCmdSync("sh ${privateScriptPath.absolutePath} $modeCommand")
            modeState.value = "$modeName 模式已激活"
            Toast.makeText(context, "$modeName 模式已激活", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        enabled = privateScriptPath.exists(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(text = modeName)
    }
}

fun copyScriptToPrivateDir(context: Context, uri: Uri, destFile: File) {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    if (inputStream != null) {
        FileOutputStream(destFile).use { output ->
            inputStream.copyTo(output)
        }
        Toast.makeText(context, "脚本已导入到软件私有目录", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "无法读取文件", Toast.LENGTH_SHORT).show()
    }
}
