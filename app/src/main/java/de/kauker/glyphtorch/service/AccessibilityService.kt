package de.kauker.glyphtorch.service

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import de.kauker.glyphtorch.LED_APP_ACTIVITY
import de.kauker.glyphtorch.LED_APP_PACKAGE
import de.kauker.glyphtorch.MainActivity
import de.kauker.glyphtorch.isPhoneUnlocked
import de.kauker.glyphtorch.service.overlay.AccessibilityOverlayManager

enum class LedLightTestState {
    OPEN,
    CLOSED,
    RESET,
    TERMINATE
}

class AccessibilityService : android.accessibilityservice.AccessibilityService() {

    private var lltState = LedLightTestState.CLOSED
    private val seekbars = mutableListOf<AccessibilityNodeInfo>()

    private var overlayManager: AccessibilityOverlayManager? = null

    private lateinit var sharedPreferences: SharedPreferences
    private var brightnessValue: Int = 0

    private var blockSystemUi: Boolean = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(event == null
            || (event.packageName == this.packageName && event.className != "${ this.packageName }.MainActivity")
            || (event.packageName == "com.android.systemui" && this.blockSystemUi)) return
        if(!isPhoneUnlocked(this)) return

        if(event.packageName != LED_APP_PACKAGE) {
            if(lltState == LedLightTestState.TERMINATE) {
                lltState = LedLightTestState.CLOSED
                closeOverlay()
            }else if(lltState == LedLightTestState.OPEN) {
                close()
            }
        }else if(event.packageName == LED_APP_PACKAGE) {
            findSeekbars()
            if(this.seekbars.size == 0) return

            when (lltState) {
                LedLightTestState.RESET -> {
                    reset()
                }
                LedLightTestState.CLOSED -> {
                    open()
                }
                else -> {}
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == "OPEN_TORCH") {
            val handler = Handler()

            if(intent.hasExtra("brightness")) {
                this.brightnessValue = intent.getIntExtra("brightness", 0)
                this.sharedPreferences.edit().putInt("brightness", this.brightnessValue).apply()
            }

            overlayManager = AccessibilityOverlayManager(this, brightnessValue)
            handler.postDelayed({ startTorchActivity(this) }, 400)

            this.blockSystemUi = true
            handler.postDelayed({ this.blockSystemUi = false }, 3000)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        this.brightnessValue = this.sharedPreferences.getInt("brightness", 256)
        super.onCreate()
    }

    override fun onInterrupt() { }

    /* B A S I C */
    private fun open() {
        lltState = LedLightTestState.OPEN

        setMasterBrightness(brightnessValue)
        repeat(4) { setGlyphBrightness(it, 4096) }

        Handler().postDelayed({
            setMasterBrightness(brightnessValue)
            repeat(4) { setGlyphBrightness(it, 4096) }
        }, 200)

        if(overlayManager != null) return
        overlayManager = AccessibilityOverlayManager(this, brightnessValue)
    }

    fun close() {
        closeOverlay()

        lltState = LedLightTestState.RESET
        Handler().postDelayed({ startTorchActivity(this) }, 250)
    }

    fun reset() {
        lltState = LedLightTestState.TERMINATE
        closeOverlay()

        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /* O V E R L A Y */
    private fun closeOverlay() {
        overlayManager?.destroy()
        overlayManager = null
    }

    /* S E E K B A R   M A N I P U L A T I O N */

    /**
     * adjusting master brightness value
     * @property value brightness value between 0 and 256
     */
    fun setMasterBrightness(value: Int) {
        brightnessValue = value
        this.sharedPreferences.edit().putInt("brightness", this.brightnessValue).apply()

        if(seekbars.size == 0) return
        setSeekbarValue(seekbars[0], value.toFloat())
    }

    /**
     * adjusting brightness of specified glyph
     * @property index index of glyph
     * @property value brightness value between 0 and 4096
     */
    private fun setGlyphBrightness(index: Int, value: Int) {
        if(seekbars.size < (index+2)) return
        setSeekbarValue(seekbars[index + 1], value.toFloat())
    }

    private fun setSeekbarValue(node: AccessibilityNodeInfo, value: Float) {
        val arguments = Bundle()
        arguments.putFloat(
            AccessibilityNodeInfo.ACTION_ARGUMENT_PROGRESS_VALUE,
            value
        )

        node.performAction(
            AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS.id,
            arguments
        )
    }

    private fun findSeekbars() {
        if(rootInActiveWindow == null) return

        seekbars.clear()
        traverse(rootInActiveWindow)
    }

    private fun traverse(node: AccessibilityNodeInfo) {
        try {
            if(node.className.contentEquals("android.widget.SeekBar")) seekbars.add(node)
            repeat(node.childCount) { traverse(node.getChild(it)) }
        }catch(_: java.lang.Exception) { }
    }

    /* S T A T I C */
    companion object {
        fun openTorch(context: Context, setBrightness: Int? = null) {
            if(!isPhoneUnlocked(context)) {
                context.startActivity(Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    .putExtra("route", "requestUnlock"))
                return
            }

            context.startService(
                Intent(context, AccessibilityService::class.java)
                    .putExtra("brightness", setBrightness)
                    .setAction("OPEN_TORCH")
            )
        }

        fun startTorchActivity(context: Context) {
            context.startActivity(Intent(Intent.ACTION_VIEW)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .setClassName(LED_APP_PACKAGE, LED_APP_ACTIVITY))
        }

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val am: AccessibilityManager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val enabledServices: List<AccessibilityServiceInfo> =
                am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)

            for (enabledService in enabledServices) {
                val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
                if(enabledServiceInfo.packageName.equals(context.packageName)) return true
            }
            return false
        }
    }
}