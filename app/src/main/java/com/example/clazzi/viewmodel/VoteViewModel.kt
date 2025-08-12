package com.example.clazzi.viewmodel

import androidx.compose.animation.core.snap
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoteViewModel : ViewModel(){
    private val _vote = MutableStateFlow<Vote?>(null)
    val vote: StateFlow<Vote?> = _vote //getter


    fun loadVote(voteId: String){ //아이디 검색해서 해당 투표 가져오기
        Firebase.firestore.collection("votes").document(voteId)
            .addSnapshotListener { snapshot, error->  //투표 정보가 바뀌었을 때 자동으로 refresh
                if(error != null){
                    //오류 처리
                    return@addSnapshotListener
                }
                if(snapshot != null){
                    _vote.value = snapshot.toObject(Vote::class.java)
                }
            }
    }
}