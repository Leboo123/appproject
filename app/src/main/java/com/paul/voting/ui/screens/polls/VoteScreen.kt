package com.paul.voting.ui.screens.polls

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.paul.voting.data.PollViewModel
import com.paul.voting.navigation.ROUTE_DASHBOARD

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

    LaunchedEffect(pollId) {
        val db = FirebaseDatabase.getInstance().reference
        db.child("polls").child(pollId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        question = snapshot.child("question").getValue(String::class.java) ?: ""
                        options = snapshot.child("options")
                            .children.mapNotNull { it.getValue(String::class.java) }
                    } else {
                        Toast.makeText(context, "Poll not found", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Poll Voting", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary),
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


                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = question.ifEmpty { "No question found" },
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = PurplePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        options.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
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
                                    colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(option, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        selectedOption?.let {
                            viewModel.vote(pollId, it) { success, error ->
                                if (success) Toast.makeText(context, "Vote recorded!", Toast.LENGTH_SHORT).show()
                                else Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        } ?: Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    enabled = selectedOption != null
                ) {
                    Text("Submit Vote", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { navController.navigate("update_poll/${pollId}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLight)
                ) {
                    Text("Update This Poll", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        val mydelete = PollViewModel()
                        mydelete.deletePoll(
                            navController = navController,
                            context = context,
                            pollId = pollId
                        )
                        navController.navigate(ROUTE_DASHBOARD)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLight)
                ) {
                    Text("Delete This Poll", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

