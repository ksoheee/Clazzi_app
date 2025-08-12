package com.example.clazzi.repository

import android.content.Context
import android.net.Uri
import com.example.clazzi.model.Vote
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow

interface VoteRepository {
    fun observeVote(): Flow<List<Vote>> //Flow 비동기적인 실시간 관찰 타입
    suspend fun addVote(vote: Vote, context: Context, imageUri: Uri)
    suspend fun setVote(vote: Vote)

    fun observeVoteById(voteId: String): Flow<Vote?>
}