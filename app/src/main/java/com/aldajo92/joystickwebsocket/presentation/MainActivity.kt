package com.aldajo92.joystickwebsocket.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aldajo92.joystickwebsocket.repository.robot_message.ConnectionState
import com.aldajo92.joystickwebsocket.ui.JoyStick
import com.aldajo92.joystickwebsocket.ui.theme.JoystickWebsocketTheme
import dagger.hilt.android.AndroidEntryPoint

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
    var textState by remember { mutableStateOf("x=0.0\ny=0.0") }

    val ipFieldState by viewModel.ipFieldState.collectAsState()
    val ipFieldValid by viewModel.isIpValid.collectAsState(false)
    val connectionState by viewModel.connectionState.collectAsState()
    val enableButtonState by viewModel.enableButtonState.collectAsState(false)

    Column(
        modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = textState
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            JoyStick(
                Modifier.align(Alignment.Center),
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
                textState = "x=$x\ny=$y"
                viewModel.setCurrentJoystickState(x, y)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = ipFieldState,
                onValueChange = {
                    val filtered = it.replace("[\\s-,]".toRegex(), "")
                    viewModel.setIP(filtered)
                },
                label = { Text(text = if (ipFieldValid) "IP" else "Invalid IP") },
                isError = !ipFieldValid,
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Button(
                modifier = Modifier.align(Alignment.CenterVertically),
                enabled = enableButtonState,
                onClick = {
                    viewModel.connect()
                    viewModel.startClock()
                }
            ) {
                Text(text = "Connect")
            }
        }
        TextConnection(
            modifier = Modifier.fillMaxWidth(),
            showConnection = connectionState == ConnectionState.Connected
        )
    }
}

@Composable
fun TextConnection(
    modifier: Modifier = Modifier,
    showConnection: Boolean = false,
) {
    AnimatedVisibility(modifier = modifier.fillMaxWidth(), visible = showConnection) {
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
