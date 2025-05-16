package com.paul.voting.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.paul.voting.data.PollViewModel
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class PollListItem(
    val id: String,
    val question: String,
    val createdAt: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dashboardscreen(navController: NavController) {
    var polls by remember { mutableStateOf<List<PollListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        PollViewModel.startListeningToPolls
    }
    LaunchedEffect(true) {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("polls")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            polls = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: return@mapNotNull null
                val question = doc.getString("question") ?: return@mapNotNull null
                val createdAt = doc.getLong("createdAt") ?: 0L
                PollListItem(id, question, createdAt)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Polling Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ))
        },        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addpoll") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Poll", tint = Color.White)
            }
        },

    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (polls.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
            }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(polls) { poll ->
                        PollCard(poll = poll) {
                            navController.navigate("vote_poll/${poll.id}")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun PollCard(poll: PollListItem, onClick: () -> Unit) {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = formatter.format(Date(poll.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = poll.question, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Created: $dateStr", style = MaterialTheme.typography.bodySmall)
        }
    }
}
