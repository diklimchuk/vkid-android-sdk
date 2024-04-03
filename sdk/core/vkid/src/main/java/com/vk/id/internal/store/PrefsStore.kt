package com.vk.id.internal.store

import android.content.Context
import androidx.preference.PreferenceManager

internal class PrefsStore(private val context: Context) {

    private val prefs
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    internal var codeVerifier: String
        get() = requireNotNull(prefs.getString(CODE_VERIFIER, DEFAULT_CODE_VERIFIER))
        set(value) = prefs.edit().putString(CODE_VERIFIER, value).apply()

    internal var state: String
        get() = requireNotNull(prefs.getString(STATE, DEFAULT_STATE))
        set(value) = prefs.edit().putString(STATE, value).apply()

    fun clear() {
        state = DEFAULT_STATE
        codeVerifier = DEFAULT_CODE_VERIFIER
    }

    companion object {
        private const val DEFAULT_STATE = ""
        private const val CODE_VERIFIER = "pkce_code_verifier"
        private const val DEFAULT_CODE_VERIFIER = ""
        private const val STATE = "state"
    }
}
