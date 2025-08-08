package com.example.clazzi

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.ui.screens.AuthScreen
import com.example.clazzi.ui.screens.CreateVoteScreen
import com.example.clazzi.ui.screens.MyPageScreen
import com.example.clazzi.ui.screens.VoteListScreen
import com.example.clazzi.ui.screens.VoteScreen
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.viewmodel.VoteListViewModel
import com.google.firebase.auth.FirebaseAuth

//@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClazziTheme {
                val navController= rememberNavController()
                val voteListViewModel = viewModel<VoteListViewModel>()
                val isLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null
                NavHost(
                    navController=navController,
                        startDestination =if(isLoggedIn)"voteList" else "auth" //초기 화면
                ) {
                    composable("auth"){
                        AuthScreen(
                            navController=navController
                        )
                    }
                    composable("myPage"){
                        MyPageScreen(
                            navController=navController
                        )
                    }
                    composable("voteList") {
                        VoteListScreen(
                            navController=navController,
                            viewModel = voteListViewModel,
                            onVoteClicked = { voteId->
                                navController.navigate("vote/$voteId")
                            }
                        )
                    }
                    composable("vote/{voteId}") {backStackEntry->
                        val voteId:String = backStackEntry.arguments?.getString("voteId")?:"1"
                        val vote = voteListViewModel.getVoteById(voteId)
                        //val vote = null
                        if(vote != null){
                            VoteScreen(
                                vote=vote,
                                navController=navController,
                                viewModel= voteListViewModel
                            )
                        }else{
                            //특정 id의 투표가 없을 때의 에러 처리
                            val context = LocalContext.current
                            Toast.makeText(context,"해당 투표가 존재하지 않습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
//                    콜백방식
//                    composable("createVote") {
//                        CreateVoteScreen(
//                            onVoteCreate = {vote->
//                                navController.popBackStack() //뒤로 가기
//                                voteListViewModel.addVote(vote)
//                            }
//                        )
//                    }
                    composable("createVote") {
                        CreateVoteScreen(
                            navController= navController,
                            viewModel= voteListViewModel,

                        )
                    }
                }

            }
        }
    }
}

