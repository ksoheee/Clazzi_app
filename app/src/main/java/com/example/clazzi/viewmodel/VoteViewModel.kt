package com.example.clazzi.viewmodel

import androidx.compose.animation.core.snap
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import com.example.clazzi.repository.FirebaseVoteRepository
import com.example.clazzi.repository.RestApiVoteRepository
import com.example.clazzi.repository.VoteRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoteViewModel(
    val voteRepository: VoteRepository
) : ViewModel(
){
    private val _vote = MutableStateFlow<Vote?>(null)
    val vote: StateFlow<Vote?> = _vote //getter

    fun loadVote(voteId: String){ //아이디 검색해서 해당 투표 가져오기
        viewModelScope.launch {
            voteRepository.observeVoteById(voteId).collect{vote ->
                _vote.value = vote
            }
        }
    }
}