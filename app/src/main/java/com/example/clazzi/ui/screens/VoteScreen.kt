package com.example.clazzi.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.clazzi.util.formatDate
import com.example.clazzi.viewmodel.VoteListViewModel
import com.example.clazzi.viewmodel.VoteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class) //TopAppBar의 실험 적인 기능 사용 하기 위해
@Composable
fun VoteScreen(
    voteId :String,
    navController : NavController,
    voteListViewModel: VoteListViewModel

){
    val voteViewModel :VoteViewModel = viewModel()  //VoteScreen 안에서만 사용하니까 여기서 선언

    //초기 데이터 로드
    LaunchedEffect(voteId){  //voteId가 바뀌기 전까지는 한번만 실행, id가 바뀌면 다시 실행
        voteViewModel.loadVote(voteId)
    }

    //vote 스테이트
    val vote = voteViewModel.vote.collectAsState().value

    //현재 파이어베이스 사용자 아이디 가져오기
    val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val currentUserId = user?.uid?: "0"

    var hasVoted by remember{mutableStateOf(false)}
    LaunchedEffect(vote){
        if(vote != null){
            hasVoted = vote.voteOptions.any{ option->  //any: voters안에 currentUserId가 하나라도 있다면 true
                option.voters.contains(currentUserId)
            }
        }
    }

    //전체 투표수
    val totalVotes: Int = vote?.voteOptions?.sumOf{it.voters.size} ?:1 //0으로 나누면 에러나므로 1로

    var selectOption by remember { mutableStateOf(0) }  //0일떄 첫번재버튼,1일때 두번째,2일때 세번째
    val coroutineScope = rememberCoroutineScope()

    //투표 마감
    var isBeforeDeadline by remember {mutableStateOf(false)}
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
                },
                actions={
                    IconButton(
                        onClick={
                            if(vote != null){
                                val voteUrl = "https://clazzi-54344.web.app/vote/${vote.id}"
                                val sendIntent = android.content.Intent().apply{
                                    action=android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, voteUrl)
                                    type ="text/plain"
                                }
                                navController.context.startActivity(Intent.createChooser(sendIntent,"투표 공유")) //팝업으로 공유창 열림
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "투표 공유")
                    }
                }
            )
        }
    ){ innerPadding ->
        //null일때 ui
        if(vote == null){
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }else{
            LaunchedEffect(vote.deadline) {
                isBeforeDeadline = vote.deadline?.let{
                    Date().before(it)  //현재 날짜가 서버에서 가져온 날짜보다 이전이면 true(데드라인>현재날짜)
                } ?: false
            }
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
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

                Text(
                    text = if(isBeforeDeadline){
                        "투표 마감: ${formatDate(vote.deadline)}"
                    }else{
                        "투표 마감"
                    }
                )
                Spacer(Modifier.height(20.dp))
                if(!hasVoted){ //투표 하지 않았을 때
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
                }else{ //투표 했을 때
                    vote.voteOptions
                        .sortedByDescending { it.voters.size }
                        .forEach{option->
                            val isMyVote= option.voters.contains(currentUserId)
                            val percent = option.voters.size.toFloat() / totalVotes

                            Column(
                                modifier=Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                    .background(if(isMyVote)Color(0xFF13F8A5).copy(0.4f)
                                    else Color.LightGray.copy(0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)

                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ){
                                    Text(
                                        text = option.optionText,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier= Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${option.voters.size}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    if(isMyVote){
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "내가 투표한 항목",
                                            tint = Color(0xFF13F8A5),
                                            modifier=Modifier.padding(start=8.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress={percent},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFF13F8A5),
                                    trackColor = Color.White.copy(alpha=0.4f),
                                )
                            }
                        }
                }


                Spacer(Modifier.height(40.dp))
                Button(
                    onClick = {
                        if(!hasVoted){//투표가 완료 되었다면
                            coroutineScope.launch{
                                val selectedOption = vote.voteOptions[selectOption]

                                //새로운 투표자를 포함한 업데이트된 투표 옵션 생성:voteOptions 여러개 중에 바뀐 항목만 바꾸어서 voteOptions 새로 만든다
                                val updateOption= selectedOption.copy(
                                    voters = selectedOption.voters +currentUserId
                                )
                                //업데이트된 투표 옵션 목록 생성:
                                val updatedOptions = vote.voteOptions.mapIndexed{index, option ->
                                    if(index == selectOption) updateOption else option
                                }
                                //
                                val updateVote = vote.copy(
                                    voteOptions = updatedOptions
                                )

                                voteListViewModel.setVote(updateVote)
                            }

                        }
                    },
                    enabled = !hasVoted && isBeforeDeadline, //투표를 하지 않았거나, 데드라인 전일 때
                    modifier = Modifier.width(200.dp)
                ){
                    Text(if(!isBeforeDeadline){
                        "투표 마감"
                        }
                        else if(hasVoted){
                            "투표 함"
                        }
                        else {
                            "투표 하기"
                        }
                    )
                }

            }
        }

    }
}