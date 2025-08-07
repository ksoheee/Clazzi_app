package com.example.clazzi.model

data class Vote(
    val id: String,
    val title: String, //투표 제목
    val voteOptions :List<VoteOption>
)

data class VoteOption(
    val id:String,
    val optionText:String,
)