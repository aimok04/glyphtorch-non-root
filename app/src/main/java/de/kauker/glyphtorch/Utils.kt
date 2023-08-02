package de.kauker.glyphtorch

import android.app.KeyguardManager
import android.content.Context

fun isPhoneUnlocked(context: Context): Boolean {
    return !(context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isKeyguardLocked
}