package com.example.clazzi.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import com.example.clazzi.model.Vote
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID


class FirebaseVoteRepository : VoteRepository {
    val db = Firebase.firestore
//
    override fun observeVote(): Flow<List<Vote>> = callbackFlow {
        val listener = db.collection("votes")
            .orderBy("createAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot,error->
                if(error!= null){
                    Log.e("Firestore","Error getting vote",error)
                    close(error)//에러가 있으면 구독을 취소하고 에러를 던져준다
                }else if(snapshot != null){ //snapshot: 가지고 오고싶은 데이터
                    val votes = snapshot.toObjects(Vote::class.java)
                    trySend(votes)
                }
            }
        awaitClose{ listener.remove() }
    }
//투표 추가
    override suspend fun addVote(
        vote: Vote,
        context: Context,
        imageUri: Uri
    ) {

        try{
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("image/${UUID.randomUUID()}.jpg")

            //이미지 업로드
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val uploadTask = inputStream?.let {imageRef.putStream(it).await()}

            //다운로드 URL 가져오기
            val downloadUrl = imageRef.downloadUrl.await().toString()

            //Firebase에 업로드할 데이터 구성
            val voteMap = hashMapOf(
                "id" to vote.id,
                "title" to vote.title,
                "imageUrl" to downloadUrl,
                "createAt" to FieldValue.serverTimestamp(),
                "voteOptions" to vote.voteOptions.map{
                    hashMapOf(
                        "id" to it.id,
                        "optionText" to it.optionText

                    )
                },
                "deadline" to vote.deadline
            )
            db.collection("votes")
                .document(vote.id)
                .set(voteMap)
                .await()
        }catch(e: Exception){
            //에러 처리(예:사용자에게 토스트 메시지 표시)
        }

    }
//투표 업데이트
    override suspend fun setVote(vote: Vote) {
        try{
            db.collection("votes")
                .document(vote.id)
                .set(vote)
                .await()
            Log.d("Firestore","투표가 성공적으로 되었습니다.")
        }catch(e:Exception){
            Log.e("Firestore","투표 업데이트 중 오류가 발생했습니다.",e)
        }
    }

    override fun observeVoteById(voteId: String): Flow<Vote?> = callbackFlow{
        val listener = db.collection("votes")
            .document(voteId)
            .addSnapshotListener { snapshot, error ->
                if(error != null){
                    close(error)
                    //오류 처리
                    return@addSnapshotListener
                }
                if(snapshot != null &&snapshot.exists()){
                    trySend(snapshot.toObject(Vote::class.java))
                }
                else{
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }
}