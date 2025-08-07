package com.example.clazzi.ui.screens

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
import androidx.navigation.NavController
import com.example.clazzi.model.Vote

@OptIn(ExperimentalMaterial3Api::class) //TopAppBar의 실험 적인 기능 사용 하기 위해
@Composable
fun VoteScreen(
    vote: Vote,
    navController : NavController
){
    var selectOption by remember { mutableStateOf(0) }  //0일떄 첫번재버튼,1일때 두번째,2일때 세번째
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
                painter= painterResource(id=android.R.drawable.ic_menu_gallery),
                contentDescription = "음식 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    selectOption=0
                },
                colors = ButtonDefaults.buttonColors(
                    if(selectOption==0) Color(0xFF13F8A5)
                            else Color.LightGray.copy(alpha = 0.5f) //투명도
                ),
                modifier = Modifier.width(200.dp)
            ){
                Text("구내식당")
            }
            Button(
                onClick = {
                    selectOption=1
                },
                colors = ButtonDefaults.buttonColors(
                    if(selectOption==1) Color(0xFF13F8A5)
                    else Color.LightGray.copy(alpha = 0.5f) //투명도
                ),
                modifier = Modifier.width(200.dp)
            ){
                Text("마라탕")
            }
            Button(
                onClick = {
                    selectOption=2
                },
                colors = ButtonDefaults.buttonColors(
                    if(selectOption==2) Color(0xFF13F8A5)
                    else Color.LightGray.copy(alpha = 0.5f) //투명도
                ),
                modifier = Modifier.width(200.dp)
            ){
                Text("돈가스")
            }
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = {},
                modifier = Modifier.width(200.dp)
            ){
                Text("투표 하기")
            }

        }
    }
}