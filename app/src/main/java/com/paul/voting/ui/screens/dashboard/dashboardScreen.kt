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
import androidx.navigation.NavHostController
import com.google.firebase.database.*
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/* ---------- data class ---------- */
data class PollListItem(
    val id: String,
    val question: String,
    val createdAt: Long
)

/* ---------- screen ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {

    /*‑‑ UI state ‑‑*/
    var polls     by remember { mutableStateOf<List<PollListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    /*‑‑ reference to "polls" node once, survives recomposition ‑‑*/
    val dbRef = remember { FirebaseDatabase.getInstance().getReference("polls") }

    /*‑‑ attach / detach the listener with DisposableEffect ‑‑*/
    DisposableEffect(dbRef) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loaded = snapshot.children.mapNotNull { child ->
                    val id        = child.key                     ?: return@mapNotNull null
                    val question  = child.child("question")
                        .getValue(String::class.java)
                        ?: child.child("title").getValue(String::class.java)
                        ?: return@mapNotNull null
                    val createdAt = child.child("createdAt")
                        .getValue(Long::class.java) ?: 0L
                    PollListItem(id, question, createdAt)
                }.sortedByDescending { it.createdAt }

                polls     = loaded
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        }

        dbRef.addValueEventListener(listener)

        onDispose { dbRef.removeEventListener(listener) }
    }

    /*‑‑ UI ‑‑*/
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Polling Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor   = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addpoll") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create poll", tint = Color.White)
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {

            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                polls.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No polls available")
                    }
                }

                else -> {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(polls) { poll ->
                            PollCard(
                                poll = poll,
                                onClick = { navController.navigate("vote_poll/${poll.id}") },
                                navController = navController

                                    /* You can inject ViewModel and call delete here if desired */

                            )

                        }
                    }
                }
            }
        }
    }
}

/* ---------- reusable card ---------- */

@Composable
fun PollCard(
    poll: PollListItem,
    onClick: () -> Unit,
    navController: NavHostController
) {
    val formatter = remember {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    }
    val dateStr = formatter.format(Date(poll.createdAt))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(poll.question, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("Created: $dateStr", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
        }
    }
}
