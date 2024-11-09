package io.github.lumyuan.turingbox.windows.main

import io.github.lumyuan.turingbox.common.shell.KeepShellPublic
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import java.io.InputStreamReader

data class CommandItem(
    val title: String,
    val description: String? = null,
    val params: List<CommandParam>? = null,
    val shell: String,
    val menu: List<MenuItem>? = null,
    val meta: Map<String, Any>? = null
)

data class CommandParam(
    val name: String,
    val label: String? = null,
    val type: String,
    val options: List<String>? = null,
    val value: Any? = null,
    val hint: String? = null,
    val required: Boolean = false
)

data class MenuItem(
    val type: String,
    val title: String,
    val autoExit: Boolean = false,
    val interruptible: Boolean = false
)

@Composable
fun ShortcutCommandsScreen(context: Context) {
    val commands = remember { loadCommands(context) }

    LazyColumn {
        items(commands) { command ->
            CommandCard(command)
        }
    }
}

fun loadCommands(context: Context): List<CommandItem> {
    val jsonFile = context.assets.open("Home.json")
    val json = InputStreamReader(jsonFile).readText()
    return Gson().fromJson(json, Array<CommandItem>::class.java).toList()
}

@Composable
fun CommandCard(command: CommandItem) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = command.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = command.description ?: "", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        command.params?.forEach { param ->
            when (param.type) {
                "checkbox" -> CheckboxWithLabel(param)
                "EditText" -> EditTextWithLabel(param)
                "Spinner" -> SpinnerWithLabel(param)
                "file" -> FilePicker(param)
                else -> {}
            }
        }

        Button(onClick = { executeShellCommand(command.shell) }) {
            Text(text = "执行命令")
        }
    }
}

@Composable
fun CheckboxWithLabel(param: CommandParam) {
    var checked by remember { mutableStateOf(param.value as Boolean?) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked == true, onCheckedChange = { checked = it })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = param.label ?: "")
    }
}

@Composable
fun EditTextWithLabel(param: CommandParam) {
    var text by remember { mutableStateOf("") }
    Column {
        Text(text = param.label ?: "")
        BasicTextField(value = text, onValueChange = { text = it })
    }
}

@Composable
fun SpinnerWithLabel(param: CommandParam) {
    var selectedOption by remember { mutableStateOf(param.options?.first()) }
    Column {
        Text(text = param.label ?: "")
        DropdownMenu(expanded = true, onDismissRequest = { /* Handle close */ }) {
            param.options?.forEach { option ->
                DropdownMenuItem(onClick = { selectedOption = option }) {
                    Text(text = option)
                }
            }
        }
    }
}

@Composable
fun FilePicker(param: CommandParam) {
    // Add file picker functionality here
    Text(text = "File Picker")
}

fun executeShellCommand(shell: String) {
    // This function will execute the shell command
}
