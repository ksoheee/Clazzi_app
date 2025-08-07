package com.example.clazzi.model

import org.checkerframework.common.value.qual.IntRangeFromGTENegativeOne
import java.util.Date

data class Vote(
    val id: String="",
    val title: String="", //투표 제목
    val createAt : Date? = null,
    val voteOptions :List<VoteOption> = emptyList()
){
    val optionCount: Int// 투표 세부 항목 개수
        get()=voteOptions.size
}

data class VoteOption(
    val id:String="",
    val optionText:String="",
    val voters:List<String> = emptyList() //각 투표의 투표자 id저장
){
    val voteCount: Int
        get()=voters.size  //몇명 투표 했는지
}