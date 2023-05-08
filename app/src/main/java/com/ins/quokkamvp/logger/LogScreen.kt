package com.ins.quokkamvp.logger

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LogScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val logs by LogDao.getInstance(context).getAll().collectAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            val scope = rememberCoroutineScope()
            Button(onClick = { scope.launch { clearLogs(context) } }) {
                Text(text = "Clear logs")
            }
        }
        items(logs) {
            Text(
                text = it.message,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }

    BackHandler() {
        onBack()
    }
}

private suspend fun clearLogs(context: Context) {
    LogDao.getInstance(context).deleteAll()
}
