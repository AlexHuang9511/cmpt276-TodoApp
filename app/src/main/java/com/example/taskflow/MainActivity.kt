package com.example.taskflow

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.taskflow.ui.theme.TaskflowTheme
import com.google.android.material.appbar.MaterialToolbar
import com.example.taskflow.TaskItem as TaskItem1

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
//import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskflowTheme {
                TaskFlow()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)

@Composable
fun TaskFlow() {
    // State to hold the list of tasks
    var taskList by remember { mutableStateOf(listOf<String>()) }
    var currentTask by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Input field for new tasks
        Spacer(modifier = Modifier.height(100.dp))

        TaskInputField(currentTask) {
            currentTask = it
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to add the new task to the list
        Button(
            onClick = {
                if (currentTask.isNotBlank()) {
                    taskList = taskList + currentTask
                    currentTask = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of tasks
        TaskList(tasks = taskList) { task ->
            taskList = taskList.filter { it != task }
        }
    }

    MaterialTheme {
        Column {
            TopAppBar(
                title = {
                    Text(text = "AppBar")
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    AppBarIcon(R.drawable.baseline_menu_24) {

                    }
                },
                actions = {
                    AppBarIcon(R.drawable.baseline_share_24) {

                    }
                    AppBarIcon(R.drawable.baseline_edit_24) {

                    }
                    AppBarIcon(R.drawable.baseline_more_vert_24) {

                    }

                }
            )
        }
    }
}

@Composable
fun AppBarIcon(icon: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null, // Provide a meaningful description for accessibility
            modifier = Modifier.size(24.dp) // Adjust size as needed
        )
    }
}


@Composable
fun TaskInputField(task: String, onTaskChange: (String) -> Unit) {
    OutlinedTextField(
        value = task,
        onValueChange = onTaskChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Enter a new task") },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { /* Handle IME action here */ })
    )
}

@Composable
fun TaskList(tasks: List<String>, onTaskRemove: (String) -> Unit) {
    LazyColumn {
        items(tasks.size) { index -> TaskItem1(task = tasks[index], onRemove = onTaskRemove) }
    }
}

@Composable
fun TaskItem(task: String, onRemove: (String) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = { onRemove(task) }) {
                Text("Done")
            }
        }
    }
}
