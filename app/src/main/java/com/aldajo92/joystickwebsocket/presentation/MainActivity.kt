package com.aldajo92.joystickwebsocket.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aldajo92.joystickwebsocket.R
import com.aldajo92.joystickwebsocket.presentation.ui.JoyStick
import com.aldajo92.joystickwebsocket.presentation.ui.theme.JoystickWebsocketTheme
import com.aldajo92.joystickwebsocket.repository.robot_message.ConnectionState
import com.aldajo92.joystickwebsocket.ui.ChartCard
import com.aldajo92.joystickwebsocket.ui.MultiXYWrapper
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            JoystickWebsocketTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {

    val ipFieldState by viewModel.ipFieldState.collectAsState()
    val ipFieldValid by viewModel.isIpValid.collectAsState(false)
    val connectionState by viewModel.connectionState.collectAsState()

    val dialogState: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    if (dialogState.value) {
        Dialog(
            onDismissRequest = { dialogState.value = false },
            content = {
                InfoDialogContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    dialogState = dialogState
                )
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = true
            )
        )
    }

    Column(
        modifier.fillMaxSize()
    ) {
        Box(Modifier.fillMaxWidth()) {
            DisplayValuesComponents(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 16.dp),
                viewModel = viewModel
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
                    .clickable {
                        dialogState.value = true
                    },
                imageVector = Icons.Filled.Info,
                contentDescription = "Information",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        CardChartWrapper(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            viewModel = viewModel
        )
        JoyStickComponent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            viewModel = viewModel
        )
        SliderComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp), viewModel = viewModel
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = ipFieldState,
                singleLine = true,
                onValueChange = {
                    val filtered = it.replace("[\\s-,]".toRegex(), "")
                    viewModel.setIP(filtered)
                },
                label = { Text(text = if (ipFieldValid) "Address" else "Invalid Address") },
                isError = !ipFieldValid,
                enabled = connectionState == ConnectionState.Disconnected
            )
            ConnectionButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onConnectClicked = viewModel::connect,
                onDisconnectClicked = viewModel::disconnect,
                showConnectLabel = connectionState == ConnectionState.Disconnected
            )
        }
        TextConnection(
            modifier = Modifier.fillMaxWidth(),
            showConnection = connectionState == ConnectionState.Connected
        )
    }
}

@Composable
fun CardChartWrapper(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
){
    val xyWrapper = remember {
        MultiXYWrapper(
            listOf(
                ColorTemplate.getHoloBlue(),
                android.graphics.Color.rgb(244, 10, 10)
            )
        )
    }
    val joyState by viewModel.joystickValueState.collectAsState()

    LaunchedEffect(joyState) {
        xyWrapper.addEntry(joyState.valueY, 0)
        xyWrapper.addEntry(joyState.valueX, 1)
    }

    ChartCard(
        modifier = modifier,
        xyWrapper = xyWrapper,
    )
}

@Composable
fun JoyStickComponent(modifier: Modifier, viewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
    ) {
        JoyStick(
            modifier = Modifier.align(Alignment.Center),
            size = 150.dp,
            dotSize = 30.dp,
            backgroundComposable = {
                Spacer(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            }
        ) { x: Float, y: Float ->
            coroutineScope.launch {
                viewModel.setCurrentJoystickState(x, y)
            }
        }
    }
}

@Composable
fun DisplayValuesComponents(modifier: Modifier, viewModel: MainViewModel) {
    val textState by viewModel.textState.collectAsState("")
    Text(
        modifier = modifier,
        text = textState
    )
}

@Composable
fun SliderComponent(modifier: Modifier, viewModel: MainViewModel) {
    val sliderValueState by viewModel.velocityMaxState.collectAsState(1f)
    Slider(
        modifier = modifier,
        value = sliderValueState,
        valueRange = 1f..3f,
        steps = 8,
        onValueChange = viewModel::setVelocityMax,
    )
}

@Preview
@Composable
fun ConnectionButton(
    modifier: Modifier = Modifier,
    onConnectClicked: () -> Unit = {},
    onDisconnectClicked: () -> Unit = {},
    showConnectLabel: Boolean = false
) {
    if (showConnectLabel) Button(
        modifier = modifier,
        onClick = onConnectClicked
    ) {
        Text(text = "Connect")
    } else Button(
        modifier = modifier,
        onClick = onDisconnectClicked,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
    ) {
        Text(
            text = "Disconnect",
            color = Color.Black
        )
    }
}

@Composable
fun TextConnection(
    modifier: Modifier = Modifier,
    showConnection: Boolean = false,
) {
    AnimatedVisibility(
        modifier = modifier.fillMaxWidth(),
        visible = showConnection,
        exit = slideOutVertically(tween(500)) + shrinkVertically(tween(500)) + fadeOut(tween(500))
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (showConnection) Color.Green else Color.Transparent
                ),
            text = if (showConnection) "Connected" else "",
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun InfoDialogContent(
    modifier: Modifier = Modifier,
    title: String = "Title",
    dialogState: MutableState<Boolean> = remember { mutableStateOf(true) },
    successButtonText: String = "Success",
    viewModel: InfoDialogViewModel = hiltViewModel(),
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(red = 28, green = 27, blue = 31)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = "Json format sent:",
                    color = Color.White
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { dialogState.value = false },
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                val clipboardManager = LocalClipboardManager.current
                val textToShow = viewModel.getJsonExample()
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .clickable {
                            clipboardManager.setText(AnnotatedString((textToShow)))
                        },
                    contentDescription = "Copy",
                    tint = Color.White.copy(alpha = 0.6f),
                    painter = painterResource(id = R.drawable.ic_copy_24)
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = textToShow,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "By: Alejandro Gómez (@aldajo92)",
                textAlign = TextAlign.End,
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}
