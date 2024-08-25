package com.mbp16.shsdishwiget.activity.mainactivityviews

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.shapes.RoundRectShape
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mbp16.shsdishwiget.activity.settingsactivityviews.GetMealSettingDataStore
import com.mbp16.shsdishwiget.utils.getRealMainPageLink
import com.mbp16.shsdishwiget.utils.getSchoolInfo
import com.mbp16.shsdishwiget.utils.getSchoolMealPageLink
import com.mbp16.shsdishwiget.utils.type1GetId
import com.mbp16.shsdishwiget.utils.type1GetMealData
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun InitialSettingsView(activity: Activity, dataStore: DataStore<Preferences>, dataFirmed: MutableState<String>) {
    val pageNumber = remember { mutableIntStateOf(1) }
    val coroutineScope = rememberCoroutineScope()

    // [교육청 코드, 학교 코드, 학교명, 주소, 사이트 주소]
    val schoolInfo = remember { mutableStateOf(listOf("", "", "", "", "")) }
    val schoolName = remember { mutableStateOf("") }
    val originType = remember { mutableStateOf("") }
    val neisAreaCode = remember { mutableStateOf("") }
    val neisSchoolCode = remember { mutableStateOf("") }
    val schoolGetType = remember { mutableIntStateOf(-1) }
    val schoolIdLink = remember { mutableStateOf("") }
    val schoolMealLink = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        dataStore.data.collect { preferences ->
            schoolName.value = preferences[GetMealSettingDataStore.schoolName] ?: ""
            originType.value = preferences[GetMealSettingDataStore.originType] ?: ""
            neisAreaCode.value = preferences[GetMealSettingDataStore.neisAreaCode] ?: ""
            neisSchoolCode.value = preferences[GetMealSettingDataStore.neisSchoolCode] ?: ""
            schoolGetType.intValue = preferences[GetMealSettingDataStore.schoolGetType] ?: -1
            schoolIdLink.value = preferences[GetMealSettingDataStore.schoolIdLink] ?: ""
            schoolMealLink.value = preferences[GetMealSettingDataStore.schoolMealLink] ?: ""
        }
    }

    fun saveDate() {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[GetMealSettingDataStore.schoolName] = schoolName.value
                preferences[GetMealSettingDataStore.originType] = originType.value
                preferences[GetMealSettingDataStore.neisAreaCode] = neisAreaCode.value
                preferences[GetMealSettingDataStore.neisSchoolCode] = neisSchoolCode.value
                preferences[GetMealSettingDataStore.schoolGetType] = schoolGetType.intValue
                preferences[GetMealSettingDataStore.schoolIdLink] = schoolIdLink.value
                preferences[GetMealSettingDataStore.schoolMealLink] = schoolMealLink.value
            }
            dataFirmed.value = "true"
        }
    }

    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = pageNumber.intValue,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(500, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> -fullWidth }
                        )
            } else {
                slideInHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing),
                    initialOffsetX = { fullWidth -> -fullWidth }
                ) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(500, easing = FastOutSlowInEasing),
                            targetOffsetX = { fullWidth -> fullWidth }
                        )
            }
        }, label = ""
    ) { targetState ->
        when (targetState) {
            1 -> { OriginTypeChoose(originType = originType) }
            2 -> { SearchSchool(schoolName, schoolInfo, neisSchoolCode, neisAreaCode, activity) }
            3 -> { if (originType.value == "neis") { Complete() }
                else { CheckLink(schoolInfo, activity) } }
            4 -> { CheckingMealPageLink(schoolInfo, schoolMealLink, pageNumber, activity) }
            5 -> { CheckingMealIDLink(schoolMealLink, schoolIdLink, schoolGetType, pageNumber, schoolInfo, activity) }
            6 -> { Complete() }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (pageNumber.intValue != 1 && !(pageNumber.intValue == 3 && originType.value == "neis") && pageNumber.intValue != 6) {
                OutlinedButton(
                    onClick = { pageNumber.intValue -= 1 },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .requiredHeight(60.dp),
                    shape = RoundedCornerShape(25),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "이전",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Button(
                onClick = {
                    if ((pageNumber.intValue == 3 && originType.value == "neis") || pageNumber.intValue == 6) {
                        saveDate()
                    } else {
                        pageNumber.intValue += 1
                    }
                },
                enabled = when (pageNumber.intValue) {
                    1 -> originType.value != ""
                    2 -> schoolName.value != ""
                    3 -> if (originType.value == "neis") true else schoolInfo.value[4] != ""
                    4 -> schoolMealLink.value != ""
                    5 -> schoolIdLink.value != ""
                    6 -> true
                    else -> false
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .requiredHeight(60.dp),
                shape = RoundedCornerShape(25)
            ) {
                Text(
                    text = when (pageNumber.intValue) {
                        3 -> if (originType.value == "neis") "완료" else "다음"
                        6 -> "완료"
                        else -> "다음"
                    },
                    style=MaterialTheme.typography.titleLarge
                )
            }
        }
    }

    BackHandler(onBack = {
        if (pageNumber.intValue > 1) {
            pageNumber.intValue -= 1
        }
    })
}

@Composable
fun OriginTypeChoose(originType: MutableState<String>) {
    val selectedColor = ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    )
    val unselectedColor = ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(text = "정보 제공처 선택", style = MaterialTheme.typography.displaySmall)
        Button(
            modifier = Modifier
                .requiredHeight(120.dp)
                .fillMaxWidth()
                .padding(20.dp, 0.dp),
            onClick = { originType.value = "neis" },
            colors = if (originType.value == "neis") selectedColor else unselectedColor,
            border =  BorderStroke(1.dp, if (originType.value == "neis") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(10),
            contentPadding = PaddingValues(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "나이스 Open API", style = MaterialTheme.typography.titleLarge)
                Text(text = "교육청에서 제공하는 급식 정보를 받아옵니다. 대부분의 학교를 지원하나 상대적으로 덜 자세하거나 부정확할 수 있습니다.")
            }
        }
        Button(
            modifier = Modifier
                .requiredHeight(120.dp)
                .fillMaxWidth()
                .padding(20.dp, 0.dp),
            onClick = { originType.value = "school" },
            colors = if (originType.value == "school") selectedColor else unselectedColor,
            border =  BorderStroke(1.dp, if (originType.value == "school") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(10),
            contentPadding = PaddingValues(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "학교 웹사이트", style = MaterialTheme.typography.titleLarge)
                Text(text = "학교 홈페이지에서 급식 정보를 크롤링하여 가져옵니다. 대체적으로 정확하고 자세한 정보를 제공하지만 지원하는 학교가 제한될 수 있습니다.")
            }
        }
    }
}

@Composable
fun SearchSchool(schoolName: MutableState<String>, schoolInfo: MutableState<List<String>>,
                 neisSchoolCode: MutableState<String>, neisAreaCode: MutableState<String>, activity: Activity) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchName = remember { mutableStateOf("") }
    val searchResult = remember { mutableStateOf(listOf(listOf(""))) }

    LaunchedEffect(Unit) {
        searchName.value = schoolName.value
    }

    LaunchedEffect(searchName.value) {
        val result = getSchoolInfo(searchName.value.replace(" ", ""), activity)
        searchResult.value = result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(text = "학교 검색", style = MaterialTheme.typography.displaySmall)
        TextField(
            value = searchName.value,
            onValueChange = { searchName.value = it },
            label = { Text("학교명") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp),
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            for (result in searchResult.value) {
                if (result.size < 5) {
                    continue
                }
                Button(
                    modifier = Modifier
                        .requiredHeight(70.dp)
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp),
                    shape = RoundedCornerShape(0),
                    contentPadding = PaddingValues(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = Color.Transparent
                    ),
                    onClick = {
                        searchName.value = result[2]
                        schoolName.value = result[2]
                        schoolInfo.value = result
                        neisAreaCode.value = result[0]
                        neisSchoolCode.value = result[1]
                        keyboardController?.hide()
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text=result[2],
                            style=MaterialTheme.typography.titleLarge,
                            color = if (result.toString() == schoolInfo.value.toString()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(text=result[3])
                    }
                }
            }
        }
    }
}

@Composable
fun CheckLink(schoolInfo: MutableState<List<String>>, activity: Activity) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val editingLink = remember { mutableStateOf(false) }
    val realLink = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        realLink.value = ""
        val schoolLink = schoolInfo.value[4]
        schoolInfo.value = schoolInfo.value.toMutableList().apply { set(4, "") }
        fun exceptionHandler() {
            realLink.value = "Error"
        }
        val thread = Thread() {
            realLink.value = getRealMainPageLink(schoolLink)
        }
        thread.setUncaughtExceptionHandler { _, _ -> exceptionHandler() }
        thread.start()
    }
    LaunchedEffect(realLink.value) {
        if (realLink.value == "Error") {
            schoolInfo.value = schoolInfo.value.toMutableList().apply { set(4, "Error") }
        } else if (realLink.value != "") {
            schoolInfo.value = schoolInfo.value.toMutableList().apply { set(4, realLink.value) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Text(
            text = "이 주소가 학교 사이트 주소가 맞나요?",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        if (editingLink.value) {
            TextField(
                value = realLink.value,
                onValueChange = { schoolInfo.value = schoolInfo.value.toMutableList().apply { set(4, it) } },
                label = { Text("학교 사이트 주소") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp),
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        editingLink.value = false
                        keyboardController?.hide()
                    }
                )
            )
        } else {
            Text(
                text = realLink.value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(schoolInfo.value[4])))
                }
            )
            Button(
                onClick = { editingLink.value = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = realLink.value != "",
                modifier = Modifier.requiredSize(60.dp),
                shape = RoundedCornerShape(20),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CheckingMealPageLink(schoolInfo: MutableState<List<String>>, schoolMealLink: MutableState<String>,
                         pageNumber: MutableIntState, activity: Activity) {
    val mealLink = remember { mutableStateOf("") }
    val alertDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        mealLink.value = ""
        schoolMealLink.value = ""
        fun exceptionHandler() {
            mealLink.value = "none"
            schoolMealLink.value = ""
            alertDialog.value = true
        }
        val thread = Thread() {
            mealLink.value = getSchoolMealPageLink(schoolInfo.value[4])
        }
        thread.setUncaughtExceptionHandler { _, _ -> exceptionHandler() }
        thread.start()
    }
    LaunchedEffect(mealLink.value) {
        if (mealLink.value == "none") {
            alertDialog.value = true
        } else if (mealLink.value != "") {
            schoolMealLink.value = mealLink.value
        }
    }

    if (alertDialog.value) {
        val uriHandler = LocalUriHandler.current
        val annotatedString = buildAnnotatedString {
            append("지원이 안되는 학교입니다. 나이스 Open API를 사용해주세요. 혹은 개발자에게 ")
            pushStringAnnotation(annotation = "mailto:phw1508@daum.net", tag = "URL")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("문의")
            }
            pop()
            append("해보시기 바랍니다.")
        }
        AlertDialog(
            onDismissRequest = { alertDialog.value = false },
            title = { Text("오류") },
            text = { ClickableText(
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                text = annotatedString,
                onClick = {
                    annotatedString.getStringAnnotations("URL", it, it).firstOrNull()?.let {
                        stringAnnotation -> uriHandler.openUri(stringAnnotation.item)
                    }

                }
            ) },
            confirmButton = {
                Button(
                    onClick = {
                        alertDialog.value = false
                        pageNumber.intValue = 1
                    }
                ) {
                    Text("확인")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        if (schoolMealLink.value == "") {
            Text(text = "급식 페이지 가져오는 중", style = MaterialTheme.typography.displaySmall)
            CircularProgressIndicator()
        } else {
            Text(text = "급식 페이지 확인", style = MaterialTheme.typography.displaySmall)
            Text(
                text = schoolMealLink.value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        activity.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(schoolMealLink.value)
                            )
                        )
                    }
                    .padding(10.dp, 0.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { alertDialog.value = true },
            ) {
                Text(text = "급식 페이지가 아니에요")
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CheckingMealIDLink(schoolMealLink: MutableState<String>, schoolIdLink: MutableState<String>,
                       schoolGetType: MutableIntState, pageNumber: MutableIntState, schoolInfo: MutableState<List<String>>,
                       activity: Activity) {
    val alertDialog = remember { mutableStateOf(false) }
    val mealData = remember { mutableStateOf(listOf("")) }
    val todayDate = remember { mutableStateOf("") }
    val idLink = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (schoolMealLink.value.contains("subMenu.do")) {
            schoolGetType.intValue = 1
            idLink.value = schoolInfo.value[4].let {
                if (it.endsWith("/")) {
                    it + "dggb/module/mlsv/selectMlsvDetailPopup.do"
                } else {
                    "$it/dggb/module/mlsv/selectMlsvDetailPopup.do"
                }
            }
        } else {
            schoolGetType.intValue = -1
            idLink.value = "none"
        }
    }
    LaunchedEffect(idLink.value) {
        val calendar = Calendar.getInstance()
        todayDate.value = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월 ${calendar.get(Calendar.DAY_OF_MONTH)}일"
        val thread: Thread
        fun exceptionHandler() {
            alertDialog.value = true
        }
        if (idLink.value == "none") {
            alertDialog.value = true
            return@LaunchedEffect
        } else if (schoolGetType.intValue == 1) {
            thread = Thread() {
                val id = type1GetId(
                    schoolMealLink.value,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    arrayListOf(arrayListOf(calendar.get(Calendar.DAY_OF_MONTH), 0))
                )
                if (id[0] == "No Data") {
                    mealData.value = listOf("No Data")
                } else {
                    mealData.value = type1GetMealData(idLink.value, id[0].toInt()).toList()
                }
                schoolIdLink.value = idLink.value
            }
        } else {
            thread = Thread() {
                exceptionHandler()
            }
        }
        thread.setUncaughtExceptionHandler { _, _ -> exceptionHandler() }
        thread.start()
    }

    if (alertDialog.value) {
        val uriHandler = LocalUriHandler.current
        val annotatedString = buildAnnotatedString {
            append("지원이 안되는 학교입니다. 나이스 Open API를 사용해주세요. 혹은 개발자에게 ")
            pushStringAnnotation(annotation = "mailto:phw1508@daum.net", tag = "URL")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("문의")
            }
            pop()
            append("해보시기 바랍니다.")
        }
        AlertDialog(
            onDismissRequest = { alertDialog.value = false },
            title = { Text("오류") },
            text = { ClickableText(
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                text = annotatedString,
                onClick = {
                    annotatedString.getStringAnnotations("URL", it, it).firstOrNull()?.let {
                            stringAnnotation -> uriHandler.openUri(stringAnnotation.item)
                    }

                }
            ) },
            confirmButton = {
                Button(
                    onClick = {
                        alertDialog.value = false
                        pageNumber.intValue = 1
                    }
                ) {
                    Text("확인")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        if (schoolIdLink.value == "") {
            Text(text = "급식 페이지 ID 가져오는 중", style = MaterialTheme.typography.displaySmall)
            CircularProgressIndicator()
        } else {
            Text(text = "테스트 급식 확인", style = MaterialTheme.typography.displaySmall)
            Column(
                modifier = Modifier.padding(20.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = todayDate.value)
                if (mealData.value[0] == "") { CircularProgressIndicator(modifier = Modifier.padding(40.dp)) }
                for (data in mealData.value) { Text(text = data) }
            }
            Button(
                onClick = { alertDialog.value = true },
            ) {
                Text(text = "테스트 급식이 이상해요")
            }
        }
    }
}

@Composable
fun Complete() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "설정 완료", style = MaterialTheme.typography.displaySmall)
    }
}