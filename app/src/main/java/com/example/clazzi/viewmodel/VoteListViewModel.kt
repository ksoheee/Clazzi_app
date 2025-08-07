package com.example.clazzi.viewmodel

import androidx.lifecycle.ViewModel
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VoteListViewModel: ViewModel() {
    private val _voteList = MutableStateFlow<List<Vote>>(emptyList())
    val voteList: StateFlow<List<Vote>> =_voteList

    init{//더미데이터를 뷰모델에서
        _voteList.value= listOf(
            Vote(id="1",title="소풍 같이가고 싶은 사람?", voteOptions = listOf(
                VoteOption(id="1", optionText = "김소희"),
                VoteOption(id="2", optionText = "웅이"),
            )
            ),
            Vote(id="2",title="어디로 놀러갈까요?", voteOptions = listOf(
                VoteOption(id="1", optionText = "강릉"),
                VoteOption(id="2", optionText = "제주도"),
                VoteOption(id="3", optionText = "부산"),
            )
            ),
            Vote(id="3",title="서핑 같이 가고 싶은 사람?", voteOptions = listOf(
                VoteOption(id="1", optionText = "김소희"),
                VoteOption(id="2", optionText = "웅이"),
                VoteOption(id="3", optionText = "엄마"),
            )
            ),
        )
    }

    //Id로 특정 투표를 가져오는 메서드
    fun getVoteById(voteId: String):Vote?{
        return _voteList.value.find{it.id==voteId}
    }
    //새로운 투표를 추가하는 메서드
    fun addVote(vote: Vote){
        _voteList.value+=vote
    }
}