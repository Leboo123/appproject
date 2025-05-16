package com.paul.voting.ui.screens.polls


import android.R.attr.contentDescription
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.paul.voting.model.Polls
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.paul.voting.data.PollViewModel
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.paul.voting.navigation.ROUTE_DASHBOARD


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePollScreen(
    viewModel: PollViewModel = viewModel(),
    navController: NavHostController,
    pollId: String
) {
    var question by remember { mutableStateOf("") }
    var optionText by remember { mutableStateOf("") }
    val context = LocalContext.current

    val currentDataRef = FirebaseDatabase.getInstance().getReference("Polls/$pollId")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val polls = snapshot.getValue(Polls::class.java)
                polls?.let {
                    question = it.question
                    optionText = it.options.joinToString(", ")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }
        }
        currentDataRef.addValueEventListener(listener)
        onDispose { currentDataRef.removeEventListener(listener) }
    }

    val PurplePrimary = Color(0xFF6200EE)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Poll", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurplePrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Poll Question") },
                placeholder = { Text("Enter poll question") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = optionText,
                onValueChange = { optionText = it },
                label = { Text("Poll Options (comma-separated)") },
                placeholder = { Text("e.g. Yes, No, Maybe") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Go Back", color = Color.White)
                }

                Button(
                    onClick = {
                        val optionsList = optionText.split(",").map { it.trim() }
                        viewModel.updatePoll(
                            updatedquestion = question,
                            updatedOptions = optionsList,
                            pollId = pollId
                        )
                        Toast.makeText(context, "Poll updated!", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_DASHBOARD)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Update", color = Color.White)
                }
            }
        }
    }
}
