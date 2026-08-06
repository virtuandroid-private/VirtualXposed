package io.virtualapp.compose.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.virtualapp.R
import io.virtualapp.compose.components.AboutListItem
import java.util.Calendar

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AboutScreen()
                }
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val context = LocalContext.current

    // State for "Thanks" dialog
    var showThanksDialog by remember { mutableStateOf(false) }

    // Resolve version name safely
    val versionName = remember(context) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Copyright Item
            item {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val copyrightText = stringResource(id = R.string.copy_right, currentYear)
                AboutListItem(title = copyrightText)
            }

            item {
                val versionTitle = stringResource(id = R.string.about_version_title, versionName)
                AboutListItem(
                    title = versionTitle,
                )
            }

            // Check Update
//            item {
//                AboutListItem(
//                    title = stringResource(id = R.string.check_update),
//                    onClick = {
//                        VAVersionService.checkUpdateImmediately(context.applicationContext, true)
//                    }
//                )
//            }

            // Thanks Dialog Trigger
            item {
                AboutListItem(
                    title = stringResource(id = R.string.about_thanks),
                    onClick = { showThanksDialog = true }
                )
            }

            // Telegram Group
            item {
                val telegramTitle = stringResource(id = R.string.about_feedback_tel_title, "VirtualXposed")
                AboutListItem(
                    title = telegramTitle,
                    onClick = {
                        openUrl(context, "https://t.me/joinchat/Gtti8Usj1JD4TchHQmy-ew")
                    }
                )
            }

            // Website
//            item {
//                AboutListItem(
//                    title = stringResource(id = R.string.about_website_title),
//                    onClick = {
//                        openUrl(context, "http://vxposed.com")
//                    }
//                )
//            }

            item {
                AboutListItem(
                    title = "GitHub / tiann",
                    onClick = {
                        openUrl(context, "https://github.com/tiann")
                    }
                )
            }
        }
    }

    // Compose Native AlertDialog replacing standard Android AlertDialog
    if (showThanksDialog) {
        AlertDialog(
            onDismissRequest = { showThanksDialog = false },
            title = { Text(text = stringResource(id = R.string.thanks_dialog_title)) },
            text = { Text(text = stringResource(id = R.string.thanks_dialog_content)) },
            confirmButton = {
                TextButton(onClick = { showThanksDialog = false }) {
                    Text(text = stringResource(id = R.string.about_icon_yes))
                }
            }
        )
    }
}



private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (_: Throwable) {
        // Safe catch for ActivityNotFoundException
    }
}