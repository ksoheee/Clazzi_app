package com.example.clazzi.repository

import android.content.Context
import android.net.Uri
import com.example.clazzi.model.Vote
import com.example.clazzi.repository.network.VoteApiService
import kotlinx.coroutines.flow.Flow

class RestApiVoteRepository(private val api: VoteApiService
): VoteRepository {
    override fun observeVote(): Flow<List<Vote>> {
        TODO("Not yet implemented")
    }

    override suspend fun addVote(
        vote: Vote,
        context: Context,
        imageUri: Uri
    ) {

    }

    override suspend fun setVote(vote: Vote) {
        TODO("Not yet implemented")
    }

    override fun observeVoteById(VoteId: String): Flow<Vote?> {
        TODO("Not yet implemented")
    }
}