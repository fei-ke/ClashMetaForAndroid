package com.github.kr328.clash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.launch
import java.util.*

class ShortcutActivity : Activity() {
    companion object {
        const val ACTION_START_SERVICE = "start_service"
        const val ACTION_STOP_SERVICE = "stop_service"
        const val ACTION_ACTIVATE_PROFILE = "activate_profile"

        fun createActionIntent(action: String): Intent {
            return Intent(Intent.ACTION_VIEW).apply {
                data = "clashmeta://shortcut/$action".toUri()
            }
        }

        fun createActivateIntent(profileUuid: String): Intent {
            return Intent(Intent.ACTION_VIEW).apply {
                data = "clashmeta://shortcut/$ACTION_ACTIVATE_PROFILE?uuid=$profileUuid".toUri()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.data?.let {
            handleUri(it)
        }

        finish()
    }

    private fun handleUri(uri: Uri) {
        when (uri.pathSegments.firstOrNull()) {
            ACTION_START_SERVICE -> startClashService()
            ACTION_STOP_SERVICE -> stopClashService()
            ACTION_ACTIVATE_PROFILE -> activateProfile(
                applicationContext,
                uri.getQueryParameter("uuid")
            )
        }
    }

    private fun activateProfile(context: Context, uuidString: String?) {
        val uuid = uuidString?.takeIf { it.isNotBlank() }
            ?.let {
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            } ?: return

        Global.launch {
            withProfile {
                queryByUUID(uuid)?.let {
                    setActive(it)
                    context.startClashService()
                }
            }
        }
    }
}