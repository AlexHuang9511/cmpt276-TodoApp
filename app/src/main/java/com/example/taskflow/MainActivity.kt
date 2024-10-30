package com.example.taskflow

import android.annotation.SuppressLint
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource

import android.app.DatePickerDialog
import android.widget.DatePicker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableFloatStateOf

import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

import android.content.SharedPreferences
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.text2.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.exp

private lateinit var sharedPreferences: SharedPreferences
private val gson = Gson()

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("taskflow_prefs", MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            TaskflowTheme {
                TaskFlow()
            }
        }
    }
}

data class Task(
    val name: String,
    val dueDate: String,
    val importance: Float
)

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)

@Composable
fun TaskFlow() {
    // State to hold the list of tasks
    var taskList by remember { mutableStateOf(loadTasks()) }
    var currentTask by remember { mutableStateOf("") }
    var taskDate by remember { mutableStateOf("") }
    var taskImportance by remember { mutableFloatStateOf(0f) }
    var isEditing by remember { mutableStateOf<Boolean>(false) }
    var editIndex by remember { mutableIntStateOf(-1) }
    var showWarning by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = "TaskFlow")
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        navigationIcon = {
            AppBarIcon(R.drawable.baseline_menu_24) {
            }
        },
        actions = {
            AppBarDateIcon(taskDate) { selectedDay ->
                taskDate = selectedDay
            }

            AppBarIcon(R.drawable.baseline_more_vert_24) {
            }
        }
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Spacer(modifier = Modifier.height(100.dp))

        TaskInputField(currentTask) {
            currentTask = it
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            if (taskDate.isNotBlank()) {
                Text(text = "Selected Day: $taskDate", modifier = Modifier.padding(8.dp))
            } else {
                Text(text = "Please select a date", modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Importance: ${taskImportance.toInt()}", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        ImportanceSlider(taskImportance) {
            taskImportance = it
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (currentTask.isNotBlank()) {
                    if (taskDate.isBlank()) {
                        showWarning = true
                    } else {
                        val newTask = Task(currentTask, taskDate, taskImportance)

                        if (isEditing) {
                            taskList = taskList.toMutableList().apply {
                                set(editIndex, newTask)
                            }
                            isEditing = false
                            editIndex = -1
                        } else {
                            taskList = taskList + newTask
                        }
                        saveTasks(taskList)
                        currentTask = ""
                        taskImportance = 0f
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Edit Task" else "Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        SortDropdownMenu()

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(taskList) { index, task ->
                TaskItem1(
                    task = task,
                    onRemove = { taskList = taskList.filter { it != task}
                        saveTasks(taskList)
                               },
                    onEdit = {
                        currentTask = task.name
                        taskDate = task.dueDate
                        taskImportance = task.importance
                        isEditing = true
                        editIndex = index
                    }
                )
            }
        }

        if (showWarning) {
            AlertDialog(
                onDismissRequest = { showWarning = false },
                title = { Text(text = "Date not selected") },
                text = { Text(text = "Please select a date before adding a task") },
                confirmButton = {
                    Button(onClick = { showWarning = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// Function to save tasks to shared preferences
private fun saveTasks(taskList: List<Task>) {
    val json = gson.toJson(taskList)
    sharedPreferences.edit().putString("task_list", json).apply()
}


// Function to load tasks from shared preferences
private fun loadTasks(): List<Task> {
    val json = sharedPreferences.getString("task_list", null) ?: return emptyList()
    val type = object : TypeToken<List<Task>>() {}.type
    return gson.fromJson(json, type)
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
fun AppBarDateIcon(taskDate: String, onDateChange: (String) -> Unit) {
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

    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    IconButton(onClick = { datePickerDialog.show() }) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_edit_calendar_24),
            contentDescription = "Choose Date",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ImportanceSlider(importance: Float, onImportanceChange: (Float) -> Unit) {
    Column {
        Slider(
            value = importance,
            onValueChange = onImportanceChange,
            valueRange = 0f..3f,
            steps = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Date", "Importance")
    var selectedItem by remember { mutableStateOf(items[0]) }

    Box() {
        TextField(
            value = selectedItem,
            onValueChange = { },
            readOnly = true,
            label = { Text("Sort") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.fillMaxWidth().clickable{ expanded = !expanded },
            enabled = false
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = item
                        expanded = false
                    },
                    text = {Text(text = item)}
                )
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onRemove: () -> Unit, onEdit: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.name)
                Text(text = "Due: ${task.dueDate}")
                Text(text = "Importance: ${task.importance.toInt()}")
            }

            Row(horizontalArrangement = Arrangement.End) {
                Button(onClick = onEdit, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Edit")
                }
                Button(onClick = onRemove) {
                    Text("Done")
                }
            }
        }
    }
}