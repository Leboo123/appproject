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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addPoll(navController: NavHostController)
  {

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Create poll") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = Color.White
                            )
                        )

                    },
                    content =
                        { padding ->
                            Column(
                                modifier = Modifier
                                    .padding(padding)
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                var pollTitle by remember { mutableStateOf(TextFieldValue("") )}
                                var options by remember { mutableStateOf(mutableListOf("", "","","")) }

                                OutlinedTextField(
                                    value=pollTitle,
                                    onValueChange = { pollTitle=it },
                                    label = { Text("Enter a poll question") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp),

                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "poll Question"
                                        )
                                    }
                                    )
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(text = "Options", style = MaterialTheme.typography.titleMedium)
                                options.forEachIndexed { index, option ->
                                    OutlinedTextField(
                                        value = option,
                                        onValueChange = { newText -> options[index] = newText },
                                        label = { Text("Option ${index + 1}") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        val cleanOptions = options.filter { it.isNotBlank() }
                                        if (pollTitle.isNotBlank() && cleanOptions.size >= 2) {
                                            // TODO: Save to Firebase or database
                                            println("Saving Poll: $pollTitle with options $cleanOptions")
                                            navController.popBackStack() // Go back after saving
                                        } else {
                                            // Show error (optional)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Create Poll")
                                }
                            }



                        })




  }


