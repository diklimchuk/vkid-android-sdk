@file:OptIn(InternalVKIDApi::class)

package com.vk.id.onetap.compose.onetap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vk.id.AccessToken
import com.vk.id.VKIDAuthFail
import com.vk.id.VKIDUser
import com.vk.id.auth.AuthCodeData
import com.vk.id.auth.Prompt
import com.vk.id.auth.VKIDAuthParams
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.common.InternalVKIDApi
import com.vk.id.multibranding.OAuthListWidget
import com.vk.id.multibranding.internal.LocalMultibrandingAnalyticsContext
import com.vk.id.multibranding.internal.MultibrandingAnalyticsContext
import com.vk.id.onetap.common.OneTapOAuth
import com.vk.id.onetap.common.OneTapStyle
import com.vk.id.onetap.compose.button.alternate.AdaptiveAlternateAccountButton
import com.vk.id.onetap.compose.button.auth.VKIDButton
import com.vk.id.onetap.compose.button.auth.VKIDButtonSmall
import com.vk.id.onetap.compose.button.auth.VKIDButtonTextProvider
import com.vk.id.onetap.compose.button.auth.rememberVKIDButtonState
import com.vk.id.onetap.compose.button.startAuth
import com.vk.id.onetap.compose.util.PlaceComposableIfFitsWidth

/**
 * Composable function to display a VKID One Tap login interface with multibranding.
 * For more information how to integrate VK ID Authentication check docs https://id.vk.com/business/go/docs/ru/vkid/latest/vk-id/intro/plan
 *
 * @param modifier Modifier for this composable.
 * @param style The styling for the One Tap interface, default is [OneTapStyle.Light]
 * @param onAuth Callback function invoked on successful authentication with an [OneTapOAuth] and an [AccessToken].
 * The first parameter is the OAuth which was used for authorization or null if the main flow with OneTap was used.
 * The second parameter is the access token to be used for working with VK API.
 * @param onAuthCode A callback to be invoked upon successful first step of auth - receiving auth code
 * which can later be exchanged to access token.
 * isCompletion is true if [onAuth] won't be called.
 * This will happen if you passed auth parameters and implement their validation yourself.
 * In that case we can't exchange auth code for access token and you should do this yourself.
 * @param onFail Callback function invoked on authentication failure with on [OneTapOAuth] and a [VKIDAuthFail] object.
 * The first parameter is the OAuth which was used for authorization or null if the main flow with OneTap was used.
 * The second parameter is the error which happened during authorization.
 * @param oAuths A set of OAuths to be displayed.
 * @param signInAnotherAccountButtonEnabled Flag to enable a button for signing into another account.
 *  Note that if text doesn't fit the available width the view will be hidden regardless of the flag.
 * @param authParams Optional params to be passed to auth. See [VKIDAuthUiParams.Builder] for more info.
 */
@Composable
@Suppress("LongMethod")
public fun OneTap(
    modifier: Modifier = Modifier,
    style: OneTapStyle = OneTapStyle.Light(),
    onAuth: (oAuth: OneTapOAuth?, accessToken: AccessToken) -> Unit,
    onAuthCode: (data: AuthCodeData, isCompletion: Boolean) -> Unit = { _, _ -> },
    onFail: (oAuth: OneTapOAuth?, fail: VKIDAuthFail) -> Unit = { _, _ -> },
    oAuths: Set<OneTapOAuth> = emptySet(),
    signInAnotherAccountButtonEnabled: Boolean = false,
    authParams: VKIDAuthUiParams = VKIDAuthUiParams {},
) {
    val coroutineScope = rememberCoroutineScope()
    var user by remember { mutableStateOf<VKIDUser?>(null) }
    if (style is OneTapStyle.Icon) {
        OneTapAnalytics.OneTapIconShown()
        VKIDButtonSmall(style = style.vkidButtonStyle, onClick = {
            val extraAuthParams = OneTapAnalytics.oneTapPressedIcon(user)
            startAuth(
                coroutineScope,
                {
                    OneTapAnalytics.authSuccessIcon()
                    onAuth(null, it)
                },
                onAuthCode,
                {
                    OneTapAnalytics.authErrorIcon(user)
                    onFail(null, it)
                },
                params = authParams.asParamsBuilder {
                    extraParams = extraAuthParams
                },
            )
        }, onUserFetched = {
            user = it
            it?.let {
                OneTapAnalytics.userWasFoundIcon()
            }
        })
    } else {
        PlaceComposableIfFitsWidth(
            modifier = modifier,
            measureModifier = Modifier.fillMaxWidth(),
            viewToMeasure = { measureModifier, measureInProgress ->
                if (!measureInProgress) {
                    OneTapAnalytics.OneTapShown()
                }
                CompositionLocalProvider(
                    LocalMultibrandingAnalyticsContext provides MultibrandingAnalyticsContext(
                        screen = "nowhere",
                        isPaused = measureInProgress
                    )
                ) {
                    OneTap(
                        modifier = measureModifier,
                        style = style,
                        oAuths = oAuths,
                        signInAnotherAccountButtonEnabled = signInAnotherAccountButtonEnabled,
                        vkidButtonTextProvider = null,
                        onVKIDButtonClick = {
                            val extraAuthParams = OneTapAnalytics.oneTapPressed(user)
                            startAuth(
                                coroutineScope,
                                {
                                    OneTapAnalytics.authSuccess()
                                    onAuth(null, it)
                                },
                                onAuthCode,
                                {
                                    OneTapAnalytics.authError(user)
                                    onFail(null, it)
                                },
                                authParams.asParamsBuilder {
                                    theme = style.toProviderTheme()
                                    extraParams = extraAuthParams
                                }
                            )
                        },
                        onAlternateButtonClick = {
                            val extraAuthParams = OneTapAnalytics.alternatePressed()
                            startAuth(
                                coroutineScope,
                                { onAuth(null, it) },
                                onAuthCode,
                                { onFail(null, it) },
                                authParams.asParamsBuilder {
                                    useOAuthProviderIfPossible = false
                                    theme = style.toProviderTheme()
                                    prompt = Prompt.LOGIN
                                    extraParams = extraAuthParams
                                }
                            )
                        },
                        onAuth = onAuth,
                        onAuthCode = onAuthCode,
                        onFail = onFail,
                        authParams = authParams,
                        onUserFetched = {
                            if (!measureInProgress) {
                                user = it
                                it?.let {
                                    OneTapAnalytics.userWasFound(signInAnotherAccountButtonEnabled)
                                }
                            }
                        }
                    )
                }
            },
            fallback = {
                OneTapAnalytics.OneTapIconShown()
                VKIDButtonSmall(style = style.vkidButtonStyle, onClick = {
                    val extraAuthParams = OneTapAnalytics.oneTapPressedIcon(user)
                    startAuth(
                        coroutineScope,
                        {
                            OneTapAnalytics.authSuccessIcon()
                            onAuth(null, it)
                        },
                        onAuthCode,
                        {
                            OneTapAnalytics.authErrorIcon(user)
                            onFail(null, it)
                        },
                        params = authParams.asParamsBuilder {
                            extraParams = extraAuthParams
                        }
                    )
                }, onUserFetched = {
                    user = it
                    it?.let { OneTapAnalytics.userWasFoundIcon() }
                })
            }
        )
    }
}

@Suppress("LongParameterList", "NonSkippableComposable")
@Composable
internal fun OneTap(
    modifier: Modifier = Modifier,
    style: OneTapStyle = OneTapStyle.Light(),
    oAuths: Set<OneTapOAuth>,
    signInAnotherAccountButtonEnabled: Boolean = false,
    vkidButtonTextProvider: VKIDButtonTextProvider?,
    onVKIDButtonClick: () -> Unit,
    onAlternateButtonClick: () -> Unit,
    onAuth: (OneTapOAuth?, AccessToken) -> Unit,
    onAuthCode: (AuthCodeData, Boolean) -> Unit,
    onFail: (OneTapOAuth?, VKIDAuthFail) -> Unit,
    authParams: VKIDAuthUiParams = VKIDAuthUiParams {},
    onUserFetched: (VKIDUser?) -> Unit,
) {
    val vkidButtonState = rememberVKIDButtonState()
    Column(modifier = modifier) {
        VKIDButton(
            modifier = Modifier.testTag("vkid_button"),
            style = style.vkidButtonStyle,
            state = vkidButtonState,
            textProvider = vkidButtonTextProvider,
            onClick = onVKIDButtonClick,
            onUserFetched = onUserFetched
        )
        if (signInAnotherAccountButtonEnabled) {
            AdaptiveAlternateAccountButton(
                vkidButtonState = vkidButtonState,
                style = style.alternateAccountButtonStyle,
                onAlternateButtonClick,
            )
        }
        if (oAuths.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            OAuthListWidget(
                onAuth = { oAuth, accessToken -> onAuth(OneTapOAuth.fromOAuth(oAuth), accessToken) },
                onAuthCode = onAuthCode,
                onFail = { oAuth, fail -> onFail(OneTapOAuth.fromOAuth(oAuth), fail) },
                style = style.oAuthListWidgetStyle,
                oAuths = oAuths.map { it.toOAuth() }.toSet(),
                authParams = authParams,
            )
        }
    }
}

private fun OneTapStyle.toProviderTheme(): VKIDAuthParams.Theme? = when (this) {
    is OneTapStyle.Dark,
    is OneTapStyle.TransparentDark -> VKIDAuthParams.Theme.Dark

    is OneTapStyle.Light,
    is OneTapStyle.TransparentLight -> VKIDAuthParams.Theme.Light

    is OneTapStyle.Icon -> null
}

@Preview
@Composable
private fun OneTapPreview() {
    OneTap(onAuth = { _, _ -> })
}
