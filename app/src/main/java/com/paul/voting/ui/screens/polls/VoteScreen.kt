package com.paul.voting.ui.screens.polls

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.paul.voting.data.PollViewModel
import kotlinx.coroutines.tasks.await

@Composable
fun VotePollScreen(pollId: String, viewModel: PollViewModel = viewModel(),navController: NavHostController) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    // Load poll data from Firestore when screen loads
    LaunchedEffect(pollId) {
        val db = FirebaseFirestore.getInstance()
        try {
            val doc = db.collection("polls").document(pollId).get().await()
            if (doc.exists()) {
                question = doc.getString("question") ?: ""
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

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Text(text = question, style = MaterialTheme.typography.headlineSmall)
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
                        )
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = { selectedOption = option }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(option)
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
                enabled = selectedOption != null
            ) {
                Text("Submit Vote")
            }
        }
    }
}
