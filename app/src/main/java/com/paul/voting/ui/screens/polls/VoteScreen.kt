package com.paul.voting.ui.screens.polls

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.paul.voting.data.PollViewModel
import com.paul.voting.navigation.ROUTE_UPDATE_POLL
import kotlinx.coroutines.tasks.await
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotePollScreen(
    pollId: String,
    viewModel: PollViewModel = viewModel(),
    navController: NavHostController
) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val PurplePrimary = Color(0xFF6200EE)
    val PurpleLight = Color(0xFFBB86FC)
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        PollViewModel.startListeningToPolls()
    }

    // Load poll data
    LaunchedEffect(pollId) {
        val db = FirebaseFirestore.getInstance()
        try {
            val doc = db.collection("polls").document(pollId).get().await()
            if (doc.exists()) {
                question = doc.getString("title") ?: doc.getString("question") ?: ""
                options = doc.get("options") as? List<String> ?: emptyList()
            } else {
                Toast.makeText(context, "Poll not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading poll: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vote", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurplePrimary
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PurplePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = question.ifEmpty { "No question found" },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                options.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .selectable(
                                selected = (option == selectedOption),
                                onClick = { selectedOption = option },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PurplePrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option, color = PurplePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedOption != null) {
                            viewModel.vote(pollId, selectedOption!!) { success, error ->
                                if (success) {
                                    Toast.makeText(context, "Vote recorded!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    enabled = selectedOption != null
                ) {
                    Text("Submit Vote", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("update_poll/$pollId")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLight)
                ) {
                    Text("Update This Poll", color = Color.White)
                }
            }
        }
    }
}


