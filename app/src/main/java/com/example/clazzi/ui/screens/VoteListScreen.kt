package com.example.clazzi.ui.screens

import android.service.controls.actions.FloatAction
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.example.clazzi.R
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.util.formatDate
import com.example.clazzi.viewmodel.VoteListViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteListScreen(
    navController : NavController,
    parentNavController: NavController,
    viewModel: VoteListViewModel,
    onVoteClicked:(String)-> Unit
){

    val voteList by viewModel.voteList.collectAsState() //뷰모델을 관찰해서 자동으로 갱신하겠다.
    Scaffold(
        topBar = {
            TopAppBar(
                title ={
                    Text(stringResource(R.string.vote_list_title))
                },
                actions = {
                    IconButton(
                        onClick = {
                            parentNavController.navigate("myPage")
                        }
                    ) {
                        Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "마이페이지"
                        )
                    }
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
            modifier = Modifier.padding(innerPadding),//.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        //modifier의 padding과 이 padding의 차이 lazyColum안에 주게 되면 밖에 padding을 주는거고 content는 content에
        ) {
            items(voteList){vote->
                VoteItem(vote) {
                    onVoteClicked(it)
                }

            }
        }

    }

}

@Composable
fun VoteItem(
    vote: Vote,
    onVoteClicked:(String)->Unit
){
    val user = FirebaseAuth.getInstance().currentUser
    val currentUserId = user?.uid?: "0"

    //hasVote 상태: 사용자가 투표했는지 판단
    var hasVoted by remember{ mutableStateOf(false) }

    //vote 데이터가 로드된 후 hasvoted 초기화
    LaunchedEffect(vote) {
        hasVoted = vote.voteOptions.any{ option->
            option.voters.contains(currentUserId)
        }
    }

    Card(
        modifier = Modifier//.padding(16.dp)
            .fillMaxWidth()
            .clickable{//투표리스트 클릭했을 때
                onVoteClicked(vote.id)
                //navController.navigate("vote") //vote로 넘어감
            }
    ){
        Row(
            modifier = Modifier.padding(16.dp)
        ){
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(vote.title,style =MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text="생성일: ${formatDate(vote.createAt)}",
                    style=MaterialTheme.typography.bodySmall
                )
                Text(
                    text="항목 개수: ${vote.optionCount}",
                    style=MaterialTheme.typography.bodySmall
                )
            }
            //투표 여부
            Text(if(hasVoted)"투표 함" else "투표 안함")
        }


    }
}

