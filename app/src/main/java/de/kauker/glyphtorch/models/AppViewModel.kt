package de.kauker.glyphtorch.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import de.kauker.glyphtorch.service.AccessibilityService

class AppViewModel(
    private val app: Application
): AndroidViewModel(app) {

    var navHostController: NavHostController? = null
    var startDestination: String = "main"

    fun isAccessibilityServiceEnabled(): Boolean {
        return AccessibilityService.isAccessibilityServiceEnabled(app)
    }

}