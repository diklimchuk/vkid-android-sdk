package com.vk.id.onetap.compose

import com.vk.id.AccessToken
import com.vk.id.VKIDAuthFail
import com.vk.id.onetap.OneTapTest
import com.vk.id.onetap.compose.onetap.OneTap

public class OneTapComposeTest : OneTapTest() {

    override fun setContent(
        onAuth: (AccessToken) -> Unit,
        onFail: (VKIDAuthFail) -> Unit,
    ) {
        composeTestRule.setContent {
            OneTap(
                onAuth = onAuth,
                onFail = onFail,
            )
        }
    }
}