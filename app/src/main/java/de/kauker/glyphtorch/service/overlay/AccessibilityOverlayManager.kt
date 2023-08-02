package de.kauker.glyphtorch.service.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.*
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import de.kauker.glyphtorch.service.AccessibilityService
import de.kauker.glyphtorch.service.MyLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AccessibilityOverlayManager(
    accessibilityService: AccessibilityService,
    defaultBrightnessValue: Int
) {

    private val wm = accessibilityService.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var layout: ComposeView? = null

    private var show by mutableStateOf(true)

    init {
        this.layout = ComposeView(accessibilityService)
        this.layout!!.setContent { AccessibilityOverlayContent(show, defaultBrightnessValue, { brightness ->
            accessibilityService.setMasterBrightness(brightness)
        }, {
            accessibilityService.close()
            Handler().postDelayed({ accessibilityService.reset() }, 400)
        }, {
            this.wm.removeView(this.layout)
        }) }

        val lifecycleOwner = MyLifecycleOwner()
        val viewModelStore = ViewModelStore()
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        this.layout!!.setViewTreeLifecycleOwner(lifecycleOwner)
        this.layout!!.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        this.layout!!.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recomposer = Recomposer(coroutineContext)
        this.layout!!.compositionContext = recomposer
        runRecomposeScope.launch { recomposer.runRecomposeAndApplyChanges() }

        val lp = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.TOP
        wm.addView(this.layout, lp)
    }

    fun destroy() {
        this.show = false
    }

}