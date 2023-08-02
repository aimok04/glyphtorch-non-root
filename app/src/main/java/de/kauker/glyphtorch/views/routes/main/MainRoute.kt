package de.kauker.glyphtorch.views.routes.main

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import de.kauker.glyphtorch.LED_APP_PACKAGE
import de.kauker.glyphtorch.R
import de.kauker.glyphtorch.models.AppViewModel
import de.kauker.glyphtorch.ui.theme.Typography
import de.kauker.glyphtorch.views.routes.main.pages.MainRouteFinalPage
import de.kauker.glyphtorch.views.routes.main.pages.MainRouteFirstPage
import de.kauker.glyphtorch.views.routes.main.pages.MainRouteSecondPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainRoute(vm: AppViewModel, bse: NavBackStackEntry) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showUnsupportedDialog by rememberSaveable { mutableStateOf(false) }

    // checking if led test app is installed
    LaunchedEffect(Unit) {
        try {
            context.packageManager.getPackageInfo(LED_APP_PACKAGE, 0)
        }catch(_: Exception) {
            showUnsupportedDialog = true
        }
    }

    if(showUnsupportedDialog) {
        AlertDialog(
            title = { Text(stringResource(id = R.string.common_unsupported)) },
            icon = { Icon(Icons.Rounded.Help, stringResource(id = R.string.common_unsupported)) },
            text = { Text(stringResource(id = R.string.route_main_alert_unsupported_body)) },
            confirmButton = { Button(onClick = {
                (context as? Activity)?.finish()
            }) {
                Text(stringResource(id = R.string.common_ok))
            }},
            onDismissRequest = { }
        )
    }

    Scaffold {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                onClick = { vm.navHostController?.navigate("legal") }
            ) {
                Icon(Icons.Rounded.MenuBook, stringResource(id = R.string.route_legal_name))
            }

            val pagerState = rememberPagerState()

            HorizontalPager(
                modifier = Modifier.padding(bottom = it.calculateTopPadding() + 36.dp),
                state = pagerState,
                userScrollEnabled = false,
                pageCount = 4
            ) { page ->
                when(page) {
                    0 -> MainRouteFirstPage(vm = vm) { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                    1 -> MainRouteSecondPage(vm = vm) { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
                    2 -> MainRouteFinalPage(vm = vm)
                }
            }
        }
    }
}

@Composable
fun MainRouteBasePage(
    title: String,
    image: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null,
    buttons: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(image != null) {
            image()
        }else if(icon != null){
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(36.dp))

        Text(
            text = title,
            style = Typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        content()

        if(buttons != null) {
            Spacer(Modifier.height(36.dp))
            buttons()
        }
    }
}