package de.kauker.glyphtorch.views.routes.main.pages

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.kauker.glyphtorch.R
import de.kauker.glyphtorch.models.AppViewModel
import de.kauker.glyphtorch.service.AccessibilityService
import de.kauker.glyphtorch.service.QuickSettingsTileService
import de.kauker.glyphtorch.views.routes.main.MainRouteBasePage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainRouteFinalPage(vm: AppViewModel) {
    val context = LocalContext.current

    MainRouteBasePage(
        title = stringResource(id = R.string.route_main_page_final_title),
        icon = Icons.Rounded.Done,
        buttons = {
            FlowRow(
                horizontalArrangement = Arrangement.Center
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) FilledTonalButton(onClick = {
                    QuickSettingsTileService.addQuickTileDialog(context)
                }) {
                    Text(stringResource(id = R.string.route_main_page_final_button_add_to_quick_settings))
                }

                Spacer(Modifier.width(16.dp))

                Button(onClick = {
                    AccessibilityService.openTorch(context)
                }) {
                    Text(stringResource(id = R.string.route_main_page_final_button_use_torch))
                }
            }
        }
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.8f),
            text = stringResource(id = R.string.route_main_page_final_body),
            textAlign = TextAlign.Center
        )
    }
}