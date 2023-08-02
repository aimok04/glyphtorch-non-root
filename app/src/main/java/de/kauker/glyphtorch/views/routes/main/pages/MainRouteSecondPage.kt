package de.kauker.glyphtorch.views.routes.main.pages

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.kauker.glyphtorch.R
import de.kauker.glyphtorch.models.AppViewModel
import de.kauker.glyphtorch.views.routes.main.MainRouteBasePage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainRouteSecondPage(vm: AppViewModel, next: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while(true) {
                if(vm.isAccessibilityServiceEnabled()) next()
                delay(1000)
            }
        }
    }
    
    MainRouteBasePage(
        title = stringResource(id = R.string.route_main_page_two_title),
        icon = Icons.Rounded.Accessibility,
        buttons = {
            Button(onClick = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }) {
                Text(stringResource(id = R.string.common_open_settings))
            }
        }
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.8f),
            text = stringResource(id = R.string.route_main_page_two_body),
            textAlign = TextAlign.Center
        )
    }
}