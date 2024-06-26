@file:OptIn(InternalVKIDApi::class)

package com.vk.id

import android.content.Context
import android.util.Base64
import com.vk.id.auth.Prompt
import com.vk.id.auth.VKIDAuthParams
import com.vk.id.common.InternalVKIDApi
import com.vk.id.internal.auth.AuthOptions
import com.vk.id.internal.auth.ServiceCredentials
import com.vk.id.internal.auth.pkce.PkceGeneratorSHA256
import com.vk.id.internal.auth.toQueryParam
import com.vk.id.internal.state.StateGenerator
import com.vk.id.internal.store.InternalVKIDPrefsStore
import java.security.SecureRandom

internal class AuthOptionsCreator(
    private val appContext: Context,
    private val pkceGenerator: Lazy<PkceGeneratorSHA256>,
    private val prefsStore: Lazy<InternalVKIDPrefsStore>,
    private val serviceCredentials: Lazy<ServiceCredentials>,
    private val stateGenerator: StateGenerator,
) {
    internal fun create(
        authParams: VKIDAuthParams
    ): AuthOptions {
        val codeChallenge = authParams.codeChallenge ?: run {
            val codeVerifier = pkceGenerator.value.generateRandomCodeVerifier(SecureRandom())
            prefsStore.value.codeVerifier = codeVerifier
            pkceGenerator.value.deriveCodeVerifierChallenge(codeVerifier)
        }
        val state = authParams.state?.also { prefsStore.value.state = it } ?: stateGenerator.regenerateState()
        val locale = authParams.locale ?: VKIDAuthParams.Locale.systemLocale(appContext)
        val theme = authParams.theme ?: VKIDAuthParams.Theme.systemTheme(appContext)
        val credentials = serviceCredentials.value
        val redirectUri = "${credentials.redirectUri}?oauth2_params=${getOAuth2Params(authParams.scopes)}"
        return AuthOptions(
            appId = credentials.clientID,
            clientSecret = credentials.clientSecret,
            codeChallenge = codeChallenge,
            codeChallengeMethod = "sha256",
            redirectUri = redirectUri,
            state = state,
            locale = locale?.toQueryParam(),
            theme = theme?.toQueryParam(),
            // To not show "Log in as..." screen in web view
            webAuthPhoneScreen = !authParams.useOAuthProviderIfPossible,
            oAuth = authParams.oAuth,
            prompt = if (authParams.prompt == Prompt.LOGIN) "login" else "",
            scopes = authParams.scopes,
            extraParams = authParams.extraParams,
        )
    }
}

private fun getOAuth2Params(scopes: Set<String>): String {
    return Base64
        .encodeToString("""{"scope":"${scopes.joinToString(separator = " ")}"}""".encodeToByteArray(), Base64.DEFAULT)
        .filter { it != '\n' }
}
