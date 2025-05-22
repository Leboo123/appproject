package com.paul.voting.data


import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.paul.voting.navigation.ROUTE_DASHBOARD
import com.paul.voting.ui.screens.dashboard.PollListItem
import java.util.UUID

class PollViewModel : ViewModel() {

    /* ------------------------------------------------------------------ */
    /*  Firebase                                                          */
    /* ------------------------------------------------------------------ */

    private val db = FirebaseDatabase.getInstance().reference
    private val auth   = FirebaseAuth.getInstance()

    private val pollsRef get() = db.child("polls")

    /* ------------------------------------------------------------------ */
    /*  UI state                                                          */
    /* ------------------------------------------------------------------ */

    var polls        by mutableStateOf<List<Poll>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /* ------------------------------------------------------------------ */
    /*  Live listener                                                     */
    /* ------------------------------------------------------------------ */

    private val pollsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val loaded = snapshot.children.mapNotNull { child ->
                val id        = child.key ?: return@mapNotNull null
                val question  = child.child("question").getValue(String::class.java) ?: return@mapNotNull null
                val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L
                PollListItem(id, question, createdAt)
            }


        }
        override fun onCancelled(error: DatabaseError) {
            errorMessage = error.message
        }
    }

    init {
        pollsRef.addValueEventListener(pollsListener)
    }

    override fun onCleared() {
        pollsRef.removeEventListener(pollsListener)
    }

    /* ------------------------------------------------------------------ */
    /*  Create                                                            */
    /* ------------------------------------------------------------------ */

    fun createPoll(
        question: String,
        options: List<String>,
        onResult: (Boolean, String?) -> Unit
    ) {
        val pollId   = UUID.randomUUID().toString()
        val votesMap = options.associateWith { 0L }

        val poll = Poll(
            id        = pollId,
            question  = question,
            options   = options,
            votes     = votesMap,
            creatorId = auth.currentUser?.uid ?: "",
            createdAt = System.currentTimeMillis()
        )

        pollsRef.child(pollId).setValue(poll)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    /* ------------------------------------------------------------------ */
    /*  Vote (anonymous, once per user)                                   */
    /* ------------------------------------------------------------------ */

    fun vote(
        pollId: String,
        selectedOption: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val userId = auth.currentUser?.uid
            ?: return onResult(false, "User not authenticated")

        val pollNode   = pollsRef.child(pollId)
        val votersNode = pollNode.child("voters/$userId")   // store boolean flag

        // Atomically check‑then‑update via transaction
        pollNode.runTransaction(object : com.google.firebase.database.Transaction.Handler {
            override fun doTransaction(curData: MutableData): com.google.firebase.database.Transaction.Result {
                val poll = curData.getValue(Poll::class.java) ?: return com.google.firebase.database.Transaction.success(curData)

                // Prevent double voting
                val voters = poll.voters ?: emptyMap()
                if (voters.containsKey(userId)) return com.google.firebase.database.Transaction.abort()

                // Increment vote
                val newVotes = poll.votes.toMutableMap()
                val current  = newVotes[selectedOption] ?: 0L
                newVotes[selectedOption] = current + 1

                // Update data
                curData.child("votes").value   = newVotes
                curData.child("voters").child(userId).value = true
                return com.google.firebase.database.Transaction.success(curData)
            }



            override fun onComplete(
                error: DatabaseError?, committed: Boolean, _snap: DataSnapshot?
            ) {
                if (error != null) {
                    onResult(false, error.message)
                } else if (!committed) {
                    onResult(false, "You have already voted on this poll.")
                } else {
                    onResult(true, null)
                }
            }
        })
    }

    /* ------------------------------------------------------------------ */
    /*  Update (only creator)                                             */
    /* ------------------------------------------------------------------ */

    fun updatePoll(
        pollId: String,
        updatedQuestion: String,
        updatedOptions: List<String>
    ) {
        val uid = auth.currentUser?.uid ?: return

        pollsRef.child(pollId).get()
            .addOnSuccessListener { snap ->
                val poll = snap.getValue(Poll::class.java)
                if (poll != null && poll.creatorId == uid) {
                    val updates = mapOf(
                        "question" to updatedQuestion,
                        "options"  to updatedOptions
                    )
                    pollsRef.child(pollId).updateChildren(updates)
                        .addOnFailureListener { errorMessage = it.message }
                } else {
                    errorMessage = "You are not authorized to update this poll."
                }
            }
            .addOnFailureListener { errorMessage = it.message }
    }

    /* ------------------------------------------------------------------ */
    /*  Delete                                                            */
    /* ------------------------------------------------------------------ */

    fun deletePoll(
        context: Context,
        pollId: String,
        navController: NavController
    ) {
        AlertDialog.Builder(context)
            .setTitle("Delete Poll")
            .setMessage("Are you sure you want to delete this poll?")
            .setPositiveButton("Yes") { _, _ ->
                pollsRef.child(pollId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Poll deleted", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Delete failed", Toast.LENGTH_LONG).show()
                        navController.navigate(ROUTE_DASHBOARD)
                    }
            }
            .setNegativeButton("No") { d, _ -> d.dismiss() }
            .show()
    }

    /* ------------------------------------------------------------------ */
    /*  Model                                                              */
    /* ------------------------------------------------------------------ */

    data class Poll(
        val id: String                  = "",
        val question: String            = "",
        val options: List<String>       = emptyList(),
        val votes: Map<String, Long>    = emptyMap(),
        val voters: Map<String, Boolean>? = null,   // Set only after first vote
        val creatorId: String           = "",
        val createdAt: Long             = 0L
    )
}
