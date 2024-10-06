package com.example.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskflow.ui.theme.TaskflowTheme
import com.example.taskflow.TaskItem as TaskItem1


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


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TaskFlow() {
    // State to hold the list of tasks
    var taskList by remember { mutableStateOf(listOf<String>()) }
    var currentTask by remember { mutableStateOf("") }
    var taskDate by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Input field for new tasks
        TaskInputField(currentTask) {
            currentTask = it
        }

        DateInputField(taskDate) {
            taskDate = it
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to add the new task to the list
        Button(
            onClick = {
                if (currentTask.isNotBlank() && taskDate.isNotBlank()) {
                    currentTask += "\nDue: " + taskDate
                    taskList = taskList + currentTask
                    currentTask = ""
                    taskDate = ""
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
fun DateInputField(date: String, onTaskChange: (String) -> Unit) {
    OutlinedTextField(
        value = date,
        onValueChange = onTaskChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Enter due date") },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task,
                modifier = Modifier
                    .weight(1f)
                    .alignByBaseline()

            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { onRemove(task) },
                modifier = Modifier.alignByBaseline()
            ) {
                Text("Done")
            }
        }
    }
}
