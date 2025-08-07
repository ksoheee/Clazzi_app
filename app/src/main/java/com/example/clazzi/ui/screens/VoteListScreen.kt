package com.example.clazzi.ui.screens

import android.service.controls.actions.FloatAction
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.ui.theme.ClazziTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteListScreen(
    navController : NavController,
    voteList: List<Vote>,
    onVoteClicked:(String)-> Unit
){

    Scaffold(
        topBar = {
            TopAppBar(
                title ={
                    Text("투표 목록")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("createVote")
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = "투표만들기")
            }
        }
    ){ innerPadding->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(voteList){vote->
                Card(
                    modifier = Modifier//.padding(16.dp)
                        .fillMaxWidth()
                        .clickable{//투표리스트 클릭했을 때
                            onVoteClicked(vote.id)
                            //navController.navigate("vote") //vote로 넘어감
                        }
                ){
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(vote.title)
                    }
                }
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
fun VoteListScreenPreview() {
    ClazziTheme {
        VoteListScreen(
            navController = NavController(LocalContext.current),
            arrayListOf
                (
                    Vote(id="1",title="소풍 같이가고 싶은 사람?", voteOptions = listOf(
                        VoteOption(id="1", optionText = "김소희"),
                        VoteOption(id="2", optionText = "웅이"),
                    )),
                    Vote(id="2",title="어디로 놀러갈까요?", voteOptions = listOf(
                        VoteOption(id="1", optionText = "강릉"),
                        VoteOption(id="2", optionText = "제주도"),
                        VoteOption(id="3", optionText = "부산"),
                    )),
                    Vote(id="3",title="서핑 같이 가고 싶은 사람?", voteOptions = listOf(
                        VoteOption(id="1", optionText = "김소희"),
                        VoteOption(id="2", optionText = "웅이"),
                        VoteOption(id="3", optionText = "엄마"),
                    )),
                ),onVoteClicked={}

        )

    }
}