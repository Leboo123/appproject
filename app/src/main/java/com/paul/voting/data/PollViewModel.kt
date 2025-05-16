package com.paul.voting.data


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.util.UUID

class PollViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    var polls by mutableStateOf<List<Poll>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    data class PublicPoll(
        val id: String,
        val title: String,
        val options: List<String>,
        val timestamp: Long
    )

    fun fetchPolls() {
        db.collection("polls")
            .get()
            .addOnSuccessListener { result ->
                polls = result.documents.mapNotNull { doc ->
                    val poll = doc.toObject(Poll::class.java)
                    poll?.let {
                        Poll(
                            id = doc.id,
                            title = it.title,
                            options = it.options,
                            timestamp = it.timestamp
                        )
                    }
                }
            }
            .addOnFailureListener {
                errorMessage = it.message
            }
    }


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
    fun updatePoll(pollId: String, updatedquestion: String, updatedOptions: List<String>) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("polls").document(pollId).get()
            .addOnSuccessListener { doc ->
                val poll = doc.toObject(Poll::class.java)
                if (poll != null && poll.creatorId == currentUserId) {
                    val updates = mapOf(
                        "title" to updatedquestion,
                        "options" to updatedOptions
                    )
                    db.collection("polls").document(pollId)
                        .update(updates)
                        .addOnSuccessListener { fetchPolls() }
                        .addOnFailureListener { errorMessage = it.message }
                } else {
                    errorMessage = "You are not authorized to update this poll."
                }
            }
            .addOnFailureListener {
                errorMessage = it.message
            }
    }
    fun startListeningToPolls() {
        db.collection("polls")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    errorMessage = error.message
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    polls = snapshots.documents.mapNotNull { it.toObject(Poll::class.java) }
                }
            }
    }
    fun getPollById(pollId: String) {
        db.collection("polls").document(pollId).get()
            .addOnSuccessListener { doc ->
                val poll = doc.toObject(Poll::class.java)
                selectedPoll = poll
            }
    }

    data class Poll(
        val id: String = "",
        val title: String = "",
        val options: List<String> = emptyList(),
        val creatorId: String = "",
        val timestamp: Long = System.currentTimeMillis()
    )
}
