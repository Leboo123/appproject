package com.paul.voting.ui.screens.polls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.paul.voting.data.AuthViewModel
import com.paul.voting.data.PollViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AddPollScreen(
    viewModel: PollViewModel = viewModel(),
    navController: NavHostController
) {
    var question by remember { mutableStateOf("") }
    var optionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create a new Poll")},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        content = { innerPadding ->  // <- required to respect scaffold insets
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {


                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = question,
                            onValueChange = { question = it },
                            label = { Text("Poll Question") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = optionText,
                                onValueChange = { optionText = it },
                                label = { Text("Add Option") },
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(onClick = {
                                if (optionText.isNotBlank()) {
                                    options = options + optionText
                                    optionText = ""
                                }
                            }) {
                                Text("Add")
                            }
                        }

                        if (options.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Options:", style = MaterialTheme.typography.titleMedium)

                            LazyColumn {
                                itemsIndexed(options) { index, option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}. $option",
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {
                                            options = options.toMutableList().also { it.removeAt(index) }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove Option"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (question.isBlank() || options.size < 2) {
                            Toast.makeText(
                                context,
                                "Please enter a question and at least two options.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.createPoll(question, options) { success, error ->
                                if (success) {
                                    Toast.makeText(context, "Poll created successfully!", Toast.LENGTH_SHORT).show()
                                    question = ""
                                    options = emptyList()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = question.isNotBlank() && options.size >= 2
                ) {
                    Text("Save Poll", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    )
}
