package com.example.clazzi.ui.components

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.material3.*
import kotlin.contracts.contract

//앱이 갤러리 접근 권한이 있는지 확인
//없으면 권한 요청
//권한이 허용되면 → 갤러리 열기
//사용자가 이미지를 고르면 Uri가 onImagePicked()로 전달됨

//권한 요청 로직
@Composable
private fun PermissionPickerLauncher(
    permission: String,         //요청할 권할 문자열
    rationale: String,          //권한이 필요한 이유를 사용자에게 설명할 텍스트
    onLaunchPicker: () -> Unit, //권한이 허용되었을 때 실행할 동작
    onResult: (Boolean) -> Unit = {}//권한 요청 결과를 전달 받는 콜백
) {
    //context:안드로이드에서 현재 앱/컴포넌트의 실행 환경 정보를 담고 있는 객체
    //읽는 순간의 context를 줌
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) } //권한 필요 이유안내 표시 여부
    var launched by remember { mutableStateOf(false) }      //중복 실행 방지 플래그

    //Compose에서 ActivityResult API를 사용하여 권한 요청 실행
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()  // contrack: 어떤 결과를 주고 받을지 규격
    ) { isGranted ->
        if (isGranted) {        //허용
            onLaunchPicker()
        } else {                //거부
            showRationale = true//showRationale로 안내 다이얼로그 표시
        }
        onResult(isGranted)
    }
    //Composable이 처음 Composition에 들어올 때 한번만 실행
    LaunchedEffect("Unit") {//키가 바뀌면 블록 재실행
        if (!launched) {        //이미 권한 있는 경우
            launched = true
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                onLaunchPicker()
                onResult(true)
            } else {
                permissionLauncher.launch(permission) //권한 요청
            }
        }
    }
    //권한을 거부 했을 때 표시
    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("권한 요청") },
            text = { Text(rationale) },
            confirmButton = {
                TextButton(onClick = { showRationale = false} ) {
                    Text("확인")
                }
            },
        )
    }
}

@Composable
fun ImagePickerWithPermission(
    onImagePicked: (Uri?) -> Unit  //Uri?가 콜백으로 전달
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }
    //런타임 권한 문자열을 OS 버전에 따라 고름
    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
    //커스텀 권한 요청 컴포저블 호출
    PermissionPickerLauncher(
        permission = imagePermission,
        rationale = "이미지를 사용하기 위해서는 갤러리 접근 권한이 필요합니다.",
        onLaunchPicker = { galleryLauncher.launch("image/*") }
    )
}

@Composable
fun CameraPickerWithPermission(
    onImageCaptured: (Uri?) -> Unit
) {
    val context =LocalContext.current
    var cameraImageUri by remember {mutableStateOf<Uri?>(null)}
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        success: Boolean ->
        if(success){
            onImageCaptured(cameraImageUri)
        }
    }
    val cameraPermission = Manifest.permission.CAMERA

    PermissionPickerLauncher(
        permission = cameraPermission,
        rationale = "사진을 촬영하려면 카메라 권한이 필요합니다.",
        onLaunchPicker = {
            cameraImageUri = createImageUri(context)
            cameraImageUri?.let{cameraLauncher.launch(it)}
        }
    )
}

fun createImageUri(context: Context): Uri?{
    val contentValues = ContentValues().apply{
        put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
        put(MediaStore.Images.Media.DISPLAY_NAME,"vote_${System.currentTimeMillis()}.jpg")
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}