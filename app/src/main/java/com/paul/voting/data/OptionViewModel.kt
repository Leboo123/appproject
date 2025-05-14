package com.paul.voting.data

import android.content.Context
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.paul.voting.model.Options


class OptionViewModel (var navController: NavController, var context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun savePoll(title: String, option1: String, option2: String, option3: String, option4: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val options = listOf(option1, option2, option3, option4).filter { it.isNotBlank() }

        if (title.isBlank() || options.size < 2) {
            onError(Exception("Poll must have a title and at least two non-blank options"))
            return
        }

        val poll = Poll(title = title, options = options)

        viewModelScope.launch {
            db.collection("polls")
                .add(poll)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onError(exception) }
        }
    }
}


