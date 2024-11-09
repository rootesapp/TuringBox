package io.github.lumyuan.turingbox.windows.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.omarea.common.shell.KeepShellPublic
import java.io.File

@Composable
fun MinePage() {
    val context = LocalContext.current
    val scriptPath = "/data/powercfg.sh"
    val scriptExists = remember { File(scriptPath).exists() }
    val modeState = remember { mutableStateOf("调度已激活") }

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
            PowerModeButton("省电", "powersave", modeState)
            PowerModeButton("均衡", "balance", modeState)
            PowerModeButton("性能", "performance", modeState)
            PowerModeButton("极速", "fast", modeState)
            PowerModeButton("自动", "auto", modeState)
        } else {
            Button(
                onClick = {
                    Toast.makeText(context, "点击下载功能正在开发", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "点击下载")
            }
        }
    }
}

@Composable
fun PowerModeButton(modeName: String, modeCommand: String, modeState: MutableState<String>) {
    val context = LocalContext.current
    Button(
        onClick = {
            KeepShellPublic.doCmdSync("sh /data/powercfg.sh $modeCommand")
            modeState.value = "$modeName 模式已激活"
            Toast.makeText(context, "$modeName 模式已激活", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        enabled = modeCommand != "powersave" // 省电模式下禁用某些按钮示例
        enabled = modeCommand != "performance" // 省电模式下禁用某些按钮示例
        enabled = modeCommand != "auto" // 省电模式下禁用某些按钮示例
        enabled = modeCommand != "fast" // 省电模式下禁用某些按钮示例
        enabled = modeCommand != "balance" // 省电模式下禁用某些按钮示例
    ) {
        Text(text = modeName)
    }
}
