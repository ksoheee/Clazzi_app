package com.example.clazzi.ui.screens

import android.R.attr.label
import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVoteScreen(
    onVoteCreate:(Vote)-> Unit
){
    val (title:String ,setTitle: (String)->Unit) = remember{mutableStateOf("")}
    val options: SnapshotStateList<String> = remember { mutableStateListOf("", "") }
    //val optionTet:List<String> = arrayListof("","")
    Scaffold(
        topBar = {
            TopAppBar(
                title ={
                    Text("투표 만들기")
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(
                value=title,
                onValueChange = setTitle,
                label ={Text("투표 제목")},
                modifier =Modifier.fillMaxWidth()
            )
            Spacer(modifier= Modifier.height(16.dp))
            Image(
                painter=painterResource(id=android.R.drawable.ic_menu_gallery),
                contentDescription = "투표 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier= Modifier.height(16.dp))
            Text("투표 항목",style= MaterialTheme.typography.titleMedium)
//            OutlinedTextField(
//                value="",
//                onValueChange = {},
//                label={Text("항목 1")},
//                modifier = Modifier.fillMaxWidth()
//
//            )

            options.forEachIndexed{index,option->
                OutlinedTextField(
                    value=option,
                    onValueChange = {newValue->
                        options[index]=newValue
                    },
                    label={Text("항목 ${index+1}")},
                    modifier = Modifier.fillMaxWidth()

                )
            }
            Button(
                onClick={
                    options.add("")
                }, //동적으로 항목추가 누르면 누르게 상태가 변경되는거니까 stat추가
                modifier=Modifier.align(Alignment.End)
            ){
                Text("항목 추가")
            }
            Button(
                onClick={
                    val newVote = Vote(
                        id = UUID.randomUUID().toString(),
                        title=title,
                        voteOptions = options
                            .filter{it.isNotBlank()}
                            .map{
                                VoteOption(id = UUID.randomUUID().toString(), optionText =it)
                            }
                    )
                    onVoteCreate(newVote)
                },
                modifier=Modifier.fillMaxSize()
            ){
                Text("투표 생성")
            }
        }
    }
}

@Composable
fun TopAppBar(){
    Box(
        modifier= Modifier
            .height(54.dp).fillMaxSize()
    )

    {
        Text("탑바")
    }
}