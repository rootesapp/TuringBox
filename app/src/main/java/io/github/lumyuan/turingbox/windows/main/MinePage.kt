package io.github.lumyuan.turingbox.windows.main

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import java.io.InputStreamReader

// 数据模型
data class AllInOne(
    val title: String,
    val desc: String,
    val summary: String,
    val color: String
)

data class Switch(
    val title: String,
    val desc: String
)

data class Text(
    val title: String
)

data class Home(
    val AllInOne: AllInOne,
    val Switch: Switch,
    val Text: Text
)

// 从assets读取并解析JSON数据
fun loadJsonFromAssets(context: Context, fileName: String): Home? {
    return try {
        val inputStream = context.assets.open(fileName)
        val json = InputStreamReader(inputStream).use { it.readText() }
        val gson = Gson()
        gson.fromJson(json, Home::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// MinePage Composable
@Composable
fun MinePage(context: Context) {
    val homeData = loadJsonFromAssets(context, "Home.json")

    homeData?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            // AllInOne部分
            AllInOneSection(it.AllInOne)

            // Switch部分
            SwitchSection(it.Switch)

            // Text部分
            TextSection(it.Text)
        }
    }
}

@Composable
fun AllInOneSection(allInOne: AllInOne) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = allInOne.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ComposeColor(android.graphics.Color.parseColor(allInOne.color))
        )
        Text(
            text = allInOne.desc,
            fontSize = 16.sp,
            color = ComposeColor.Black
        )
        Text(
            text = allInOne.summary,
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            color = ComposeColor(android.graphics.Color.parseColor(allInOne.color))
        )
    }
}

@Composable
fun SwitchSection(switch: Switch) {
    var isChecked by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = switch.title,
            fontSize = 16.sp,
            color = ComposeColor.Black
        )
        Text(
            text = switch.desc,
            fontSize = 14.sp,
            color = ComposeColor.Gray
        )

        // Switch 控件
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = ComposeColor(0xFF9C27B0))
        )
    }
}

@Composable
fun TextSection(text: Text) {
    Text(
        text = text.title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ComposeColor.Black
    )
}
