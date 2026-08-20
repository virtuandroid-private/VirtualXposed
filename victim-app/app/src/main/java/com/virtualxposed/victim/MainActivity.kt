package com.virtualxposed.victim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.virtualxposed.victim.ui.theme.VictimAppTheme
import java.io.File


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VictimAppTheme {
                val viewModel: MainViewModel = viewModel(factory = viewModelFactory {
                    initializer {
                        MainViewModel().also {
                            val privateDir = this@MainActivity.filesDir
                            it.createPrivateFile(privateDir)
                        }
                    }
                })

                val state = viewModel.state.collectAsState()
                StartScreen(state.value)
            }
        }
    }
}

@Composable
fun StartScreen(state: MainState, modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            Alignment.Center,
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "Network data: ${state.data}",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
                Text(
                    text = "Version: ${state.version}",
                    Modifier.padding(10.dp)
                )
                Text(
                    text = "Fingerprint: ${state.fingerprint}",
                    Modifier.padding(10.dp)
                )
                Text(
                    text = "Private file content: ${state.fileContent}",
                    modifier = Modifier.padding(10.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VictimAppTheme {
        StartScreen(MainState())
    }
}