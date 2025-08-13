package com.example.clazzi

import android.R.attr.label
import android.R.attr.onClick
import android.net.http.SslCertificate.saveState
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.repository.FirebaseVoteRepository
import com.example.clazzi.repository.RestApiVoteRepository
import com.example.clazzi.repository.network.ApiClient
import com.example.clazzi.ui.screens.AuthScreen
import com.example.clazzi.ui.screens.ChatScreen
import com.example.clazzi.ui.screens.CreateVoteScreen
import com.example.clazzi.ui.screens.MyPageScreen
import com.example.clazzi.ui.screens.VoteListScreen
import com.example.clazzi.ui.screens.VoteScreen
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.viewmodel.VoteListViewModel
import com.example.clazzi.viewmodel.VoteListViewModelFactory
import com.example.clazzi.viewmodel.VoteViewModel
import com.example.clazzi.viewmodel.VoteViewModelFactory
import com.google.firebase.auth.FirebaseAuth


//@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            ClazziTheme {
                val navController= rememberNavController()

                val repo = FirebaseVoteRepository()  //파이어베이스 연동
                //val repo = RestApiVoteRepository(ApiClient.voteApiService)   //RESTAPI 연동
                val voteListViewModel: VoteListViewModel = viewModel(
                    factory = VoteListViewModelFactory(repo)
                )
                val voteViewModel: VoteViewModel = viewModel(
                    factory = VoteViewModelFactory(repo)
                )
                val isLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null

                NavHost(
                    navController=navController,
                    startDestination =if(isLoggedIn)"main" else "auth" //초기 화면
                ) {
                    composable("auth"){
                        AuthScreen(
                            navController=navController
                        )
                    }
                    composable("main") {
                        MainScreen(
                            voteListViewModel,
                            navController
                        )
                    }

                    composable(
                        "vote/{voteId}",
                        deepLinks=listOf(
                            navDeepLink { uriPattern="clazzi://vote/{voteId}" }, //스키마 형식
                            navDeepLink { uriPattern="https://clazzi.web.app/vote/{voteId}" } //파이어베이스에서 제공해주는
                        )
                    ) {backStackEntry->
                        val voteId:String = backStackEntry.arguments?.getString("voteId")?:"1"
                        VoteScreen(
                            voteId=voteId, //vote를 직접 넘기지 않고 id로 넘김, id만 넘기고 그 id로 vote를 가져오니까 중간에 vote가 바뀌어도 괜찮음
                            navController=navController,
                            voteViewModel= voteViewModel,
                            voteListViewModel= voteListViewModel
                        )
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
                    composable("myPage"){
                        MyPageScreen(
                            navController=navController
                        )
                    }
                }

            }
        }
    }
}
sealed class BottomNavItem(val route: String, val icon: ImageVector,val label: String){
    object VoteList : BottomNavItem("voteList", Icons.AutoMirrored.Filled.List,"투표")
    object Chat : BottomNavItem("chat", Icons.Filled.ChatBubble,"채팅")
    object MyPage : BottomNavItem("myPage", Icons.Filled.AccountCircle ,"마이페이지")
}

//Scffold안에서 NavHost에서는 그 안에서만 사용할 수 있음, parentNavController는 그 위에 선언되어 있기 때문에 상위에서 사용가능
@Composable
fun MainScreen(
    voteListViewModel: VoteListViewModel,
    parentNavController: NavHostController
){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding->
        NavHost(
            navController = navController,
            startDestination = "voteList",
            modifier = Modifier.padding(innerPadding)
        ){
            composable(BottomNavItem.VoteList.route) {
                VoteListScreen(
                    navController=navController,
                    parentNavController= parentNavController,
                    viewModel = voteListViewModel,
                    onVoteClicked = { voteId->
                        parentNavController.navigate("vote/$voteId")
                    }
                )
            }
            composable(BottomNavItem.Chat.route){
                ChatScreen()
            }
            composable(BottomNavItem.MyPage.route){
                MyPageScreen(
                    navController=parentNavController
                )
            }
        }

    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController){
    val items=listOf(
        BottomNavItem.VoteList,
        BottomNavItem.Chat,
        BottomNavItem.MyPage,
    )
    BottomNavigation{
        val currentRoute = navController
            .currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item->
            BottomNavigationItem(

                icon={
                    Icon(item.icon, contentDescription = item.label)
                },
                label={Text(item.label)},
                selected = currentRoute == item.route,
                onClick ={
                    navController.navigate(item.route)
                    {
                        popUpTo(navController.graph.startDestinationId){
                            saveState= true
                        }
                        launchSingleTop=true
                        restoreState = true
                    }
                }
            )

        }

    }
}
