package com.example.clazzi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.ui.screens.CreateVoteScreen
import com.example.clazzi.ui.screens.VoteListScreen
import com.example.clazzi.ui.screens.VoteScreen
import com.example.clazzi.ui.theme.ClazziTheme

//@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClazziTheme {
                val navController= rememberNavController()
                val voteList =remember{ mutableStateListOf(  //mutableListOf->mutableStateListOf
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
                NavHost(
                    navController=navController,
                        startDestination = "voteList" //초기 화면
                        //startDestination = "createVote" //초기 화면
                ) {
                    composable("voteList") {
                        VoteListScreen(
                            navController=navController,
                            voteList= voteList,
                            onVoteClicked = { voteId->
                                navController.navigate("vote/$voteId")
                            }
                        )
                    }
                    composable("vote/{voteId}") {backStackEntry->
                        val voteId:String = backStackEntry.arguments?.getString("voteId")?:"1"
                        VoteScreen(
                            vote=voteList.first{ vote ->
                                vote.id==voteId
                            },
                            navController=navController
                        )
                    }
                    composable("createVote") {
                        CreateVoteScreen(
                            onVoteCreate = {vote->
                                navController.popBackStack()
                                voteList.add(vote)
                            }
                        )
                    }
                }

            }
        }
    }
}

