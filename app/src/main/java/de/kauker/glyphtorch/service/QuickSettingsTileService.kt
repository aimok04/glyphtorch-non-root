package de.kauker.glyphtorch.service

import android.annotation.SuppressLint
import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import de.kauker.glyphtorch.MainActivity
import de.kauker.glyphtorch.R
import java.util.concurrent.Executor

class QuickSettingsTileService : TileService() {

    override fun onClick() {
        if(!AccessibilityService.isAccessibilityServiceEnabled(this)) {
            startActivityAndCollapse(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val newBrightness = if(sp.getInt("brightness", 0) == 0) 255 else 0

        AccessibilityService.openTorch(this, newBrightness)
        super.onClick()
    }

    override fun onStartListening() {
        val brightness = PreferenceManager.getDefaultSharedPreferences(this).getInt("brightness", 0)
        this.qsTile.state = if(brightness > 0) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        this.qsTile.updateTile()

        super.onStartListening()
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @SuppressLint("WrongConstant")
        fun addQuickTileDialog(context: Context) {
            val resultSuccessExecutor = Executor {  }

            val manager = context.getSystemService(Context.STATUS_BAR_SERVICE) as StatusBarManager
            manager.requestAddTileService(
                ComponentName(
                    context,
                    QuickSettingsTileService::class.java
                ),
                context.getString(R.string.function_name),
                Icon.createWithResource(context, R.drawable.ic_glyph_torch_tile_icon),
                resultSuccessExecutor
            ) {}
        }
    }

}