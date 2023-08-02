package de.kauker.glyphtorch.views.routes.requestUnlock

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import de.kauker.glyphtorch.models.AppViewModel
import de.kauker.glyphtorch.service.AccessibilityService
import kotlinx.coroutines.delay

@Composable
fun RequestUnlockRoute(vm: AppViewModel, bse: NavBackStackEntry) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        while(true) {
            if(!keyguardManager.isKeyguardLocked) {
                AccessibilityService.openTorch(context)
                delay(500)
                (context as? Activity)?.finish()
                break
            }

            delay(200)
        }
    }
}