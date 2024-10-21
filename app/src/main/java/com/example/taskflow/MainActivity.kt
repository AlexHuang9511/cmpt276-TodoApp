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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider//for slider
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
import com.example.taskflow.ui.theme.TaskflowTheme
import com.example.taskflow.TaskItem as TaskItem1

import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource

import android.app.DatePickerDialog//for date picker
import android.content.Context
import android.content.SharedPreferences
import android.widget.DatePicker//for date picker

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.width

import androidx.compose.runtime.mutableFloatStateOf

import androidx.compose.ui.platform.LocalContext
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import java.util.Calendar//for now date

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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

@Serializable
data class Task(
    val currTask: String,
    val date: String,
    val importance: Float
)

private const val PREFS_NAME = "task_prefs"
private const val TASKS_KEY = "tasks"

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TaskFlow() {
    val context = LocalContext.current

    // State to hold the list of tasks
    var taskList by remember { mutableStateOf(loadTasks(context)) }
    var currentTask by remember { mutableStateOf("") }
    var taskDate by remember { mutableStateOf("") }
    var taskImportance by remember { mutableFloatStateOf(0f) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Input field for new tasks
        Spacer(modifier = Modifier.height(100.dp))

        TaskInputField(currentTask) {
            currentTask = it
        }

        DateInputField(taskDate) {
            taskDate = it
        }

        Spacer(modifier = Modifier.height(8.dp))
        //importance slider
        ImportanceSlider(taskImportance) {
            taskImportance = it
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to add the new task to the list
        Button(
            onClick = {
                if (currentTask.isNotBlank() && taskDate.isNotBlank()) {
                    /*
                    currentTask += "\nDue: " + taskDate + "\nImportance: " + taskImportance.toInt()
                    taskList = taskList + currentTask
                    currentTask = ""
                    taskDate = ""
                    taskImportance = 0f
                    */
                    val newTask = Task(currentTask, taskDate, taskImportance)

                    taskList = taskList + newTask

                    saveTasks(context, taskList)

                    currentTask = ""
                    taskDate = ""
                    taskImportance = 0f
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
fun DateInputField(date: String, onDateChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            onDateChange("$selectedYear/${selectedMonth + 1}/$selectedDayOfMonth")
        }, year, month, day
    )

    Text(
        text = date.ifEmpty { "Choose a Date" },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
            .padding(16.dp)
    )
}

@Composable
fun ImportanceSlider(importance: Float, onImportanceChange: (Float) -> Unit) {
    Column {
        Text("Importance: ${importance.toInt()}")
        Slider(
            value = importance,
            onValueChange = onImportanceChange,
            //importance slider is for 0 to 3, i think it is enough
            // alex - made it 5 for a good round number
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// save tasks into storage
fun saveTasks(context: Context, tasks: List<Task>) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val json = Json.encodeToString(tasks)  // Serialize task list to JSON
    editor.putString(TASKS_KEY, json)
    editor.apply()
}

// get tasks from storage
fun loadTasks(context: Context): List<Task> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(TASKS_KEY, null)
    return if (json != null) {
        try {
            Json.decodeFromString(json)  // Deserialize JSON back to a list of Task objects
        } catch (e: Exception) {
            emptyList()
        }
    } else {
        emptyList()  // Return empty list if no tasks are saved
    }
}

@Composable
fun TaskList(tasks: List<Task>, onTaskRemove: (Task) -> Unit) {
    LazyColumn {
        items(tasks.size) { index ->
            TaskItem1(task = tasks[index], onRemove = onTaskRemove)
        }
    }
}

@Composable
fun TaskItem(task: Task, onRemove: (Task) -> Unit) {
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
            Column (modifier = Modifier.weight(1f)) {
                Text(text = task.currTask)
                Text(text = "Due: ${task.date}")
                Text(text = "Importance: ${task.importance.toInt()}")

            }


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
