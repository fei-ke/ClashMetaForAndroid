package com.github.kr328.clash

import android.content.Context
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.github.kr328.clash.util.withProfile

object Shortcuts {
    suspend fun install(context: Context) {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)

        withProfile {
            queryAll().map {
                ShortcutInfoCompat.Builder(context, it.uuid.toString())
                    .setShortLabel(it.name)
                    .setIcon(IconCompat.createWithResource(context, R.drawable.ic_clash))
                    .setIntent(ShortcutActivity.createActivateIntent(it.uuid.toString()))
                    .build()
            }.let {
                ShortcutManagerCompat.addDynamicShortcuts(context, it)
            }
        }
        
        val start = ShortcutInfoCompat.Builder(context, "start-service")
            .setShortLabel(context.getString(R.string.start_service))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_clash))
            .setIntent(ShortcutActivity.createActionIntent(ShortcutActivity.ACTION_START_SERVICE))
            .build()

        val stop = ShortcutInfoCompat.Builder(context, "stop-service")
            .setShortLabel(context.getString(R.string.stop_service))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_clash))
            .setIntent(ShortcutActivity.createActionIntent(ShortcutActivity.ACTION_STOP_SERVICE))
            .build()
        ShortcutManagerCompat.addDynamicShortcuts(context, listOf(start, stop))

    }
}