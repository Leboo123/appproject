package com.paul.voting.data


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.util.UUID

class PollViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Create a new poll with empty votes
    fun createPoll(question: String, options: List<String>, onResult: (Boolean, String?) -> Unit) {
        val pollId = UUID.randomUUID().toString()
        val votesMap = options.associateWith { 0 } // initialize all options with 0 votes

        val poll = mapOf(
            "id" to pollId,
            "question" to question,
            "options" to options,
            "votes" to votesMap,
            "createdAt" to System.currentTimeMillis()
        )

        viewModelScope.launch {
            db.collection("polls")
                .document(pollId)
                .set(poll, SetOptions.merge())
                .addOnSuccessListener {
                    onResult(true, null)
                }
                .addOnFailureListener { e ->
                    onResult(false, e.message)
                }
        }
    }

    // Anonymous voting – only increments the selected option's count
    fun vote(pollId: String, selectedOption: String, onResult: (Boolean, String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(false, "User not authenticated")
        val voteRef = db.collection("polls").document(pollId)

        viewModelScope.launch {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(voteRef)

                // Check if user has already voted
                val voters = snapshot.get("voters") as? Map<*, *> ?: emptyMap<String, Boolean>()
                if (voters.containsKey(userId)) {
                    throw Exception("You have already voted on this poll.")
                }

                val currentVotes = snapshot.get("votes") as? Map<*, *> ?: emptyMap<String, Boolean>()
                val currentCount = (currentVotes[selectedOption] as? Long) ?: 0

                // Prepare updates
                val updates = mapOf(
                    "votes.$selectedOption" to currentCount + 1,
                    "voters.$userId" to true
                )

                transaction.update(voteRef, updates)
            }.addOnSuccessListener {
                onResult(true, null)
            }.addOnFailureListener { e ->
                onResult(false, e.message)
            }
        }
    }

}
