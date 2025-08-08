package com.example.clazzi.ui.screens

import android.R.attr.contentDescription
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.clazzi.model.Vote
import com.example.clazzi.viewmodel.VoteListViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class) //TopAppBar의 실험 적인 기능 사용 하기 위해
@Composable
fun VoteScreen(
    vote: Vote,
    navController : NavController,
    viewModel: VoteListViewModel
){
    var selectOption by remember { mutableStateOf(0) }  //0일떄 첫번재버튼,1일때 두번째,2일때 세번째
    var hasVoted by remember{mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title ={Text("투표")},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally//수평, 가운데
        ) {
            Text(
                text= buildAnnotatedString {
                    append("친구들과 ")
                    withStyle(style= SpanStyle(fontWeight = FontWeight.Bold)){
                        append("서로 투표")
                    }
                    append("하며\n")
                    withStyle(style= SpanStyle(fontWeight = FontWeight.Bold)){
                        append("익명")
                    }
                    append("으로 마음을 전해요")
                },
                fontSize=18.sp,
            )
            Spacer(Modifier.height(40.dp)) //여백을 주기 위해
            Text(
                text =vote.title,
                style = TextStyle(
                    fontSize = 20.sp, //14가 기본
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(20.dp))
            Image(
                painter = if(vote.imageUrl != null)
                    rememberAsyncImagePainter(vote.imageUrl)
                else
                    painterResource(id=android.R.drawable.ic_menu_gallery),
                contentDescription = "투표 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(Modifier.height(20.dp))

            vote.voteOptions.forEachIndexed { index, voteOption ->
                Button(
                    onClick = {
                        selectOption=index
                    },
                    colors = ButtonDefaults.buttonColors(
                        if(selectOption==index) Color(0xFF13F8A5)
                        else Color.LightGray.copy(alpha = 0.5f) //투명도
                    ),
                    modifier = Modifier.width(200.dp)
                ){
                    Text(voteOption.optionText)
                }
            }

            Spacer(Modifier.height(40.dp))
            Button(
                onClick = {
                    if(!hasVoted){//투표가 완료 되었다면
                        coroutineScope.launch{
                            val user = FirebaseAuth.getInstance().currentUser
                            val uid = user?.uid?:"0" //null이면 0

//                            val voterId = UUID.randomUUID().toString()
//                            val selectedOption = vote.voteOptions[selectOption]
                            val voterId = uid
                            val selectedOption = vote.voteOptions[selectOption]

                            //새로운 투표자를 포함한 업데이트된 투표 옵션 생성:voteOptions 여러개 중에 바뀐 항목만 바꾸어서 voteOptions 새로 만든다
                            val updateOption= selectedOption.copy(
                                voters = selectedOption.voters +voterId
                            )
                            //업데이트된 투표 옵션 목록 생성:
                            val updatedOptions = vote.voteOptions.mapIndexed{index, option ->
                                if(index == selectOption) updateOption else option
                            }
                            //
                            val updateVote = vote.copy(
                                voteOptions = updatedOptions
                            )

                            viewModel.setVote(updateVote)
                            hasVoted=true
                        }

                    }
                },
                enabled = !hasVoted,
                modifier = Modifier.width(200.dp)
            ){
                Text("투표 하기")
            }

        }
    }
}