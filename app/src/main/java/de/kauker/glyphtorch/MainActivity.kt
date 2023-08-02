package de.kauker.glyphtorch

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.kauker.glyphtorch.models.AppViewModel
import de.kauker.glyphtorch.ui.theme.GlyphTorchNonRootTheme
import de.kauker.glyphtorch.views.main.MainRouter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = AppViewModel(this.application)
        vm.startDestination = this.intent.getStringExtra("route")?: "main"

        if(vm.startDestination == "requestUnlock") {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

        setContent {
            GlyphTorchNonRootTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainRouter(vm)
                }
            }
        }
    }
}

