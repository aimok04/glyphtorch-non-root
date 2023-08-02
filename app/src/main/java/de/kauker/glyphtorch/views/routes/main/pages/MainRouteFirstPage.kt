package de.kauker.glyphtorch.views.routes.main.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.kauker.glyphtorch.R
import de.kauker.glyphtorch.models.AppViewModel
import de.kauker.glyphtorch.views.routes.main.MainRouteBasePage

@Composable
fun MainRouteFirstPage(vm: AppViewModel, next: () -> Unit) {
    MainRouteBasePage(
        title = stringResource(id = R.string.app_name),
        image = {
            Image(
                painter = painterResource(id = R.drawable.splash_icon),
                contentDescription = stringResource(id = R.string.app_name)
            )
        },
        buttons = {
            Button(onClick = next) {
                Text(stringResource(id = R.string.common_next))
            }
        }
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.8f),
            text = stringResource(id = R.string.route_main_introduction_body),
            textAlign = TextAlign.Center
        )
    }
}