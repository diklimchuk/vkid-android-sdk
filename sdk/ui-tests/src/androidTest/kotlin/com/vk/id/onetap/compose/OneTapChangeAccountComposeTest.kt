package com.vk.id.onetap.compose

import com.vk.id.AccessToken
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.AuthCodeData
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.common.allure.Feature
import com.vk.id.common.feature.TestFeature
import com.vk.id.onetap.base.ChangeAccountTest
import com.vk.id.onetap.common.OneTapOAuth
import com.vk.id.onetap.compose.onetap.OneTap
import io.qameta.allure.kotlin.AllureId
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

@Feature(TestFeature.ONE_TAP)
@DisplayName("Смена аккаунта в Compose OneTap")
public class OneTapChangeAccountComposeTest : ChangeAccountTest() {

    @Test
    @AllureId("2289802")
    @DisplayName("Успешное получение токена в Compose OneTap смене аккаунта")
    override fun tokenIsReceived() {
        super.tokenIsReceived()
    }

    @Test
    @AllureId("2302959")
    @DisplayName("Успешное получение токена после логаута в Compose OneTap")
    override fun tokenIsReceivedAfterFailedLogout() {
        super.tokenIsReceivedAfterFailedLogout()
    }

    @Test
    @AllureId("2289870")
    @DisplayName("Получение ошибочного редиректа в Activity в Compose OneTap смене аккаунта")
    override fun failedRedirectActivityIsReceived() {
        super.failedRedirectActivityIsReceived()
    }

    @Test
    @AllureId("2289581")
    @DisplayName("Получение ошибки отсутсвия браузера в Compose OneTap смене аккаунта")
    override fun noBrowserAvailableIsReceived() {
        super.noBrowserAvailableIsReceived()
    }

    @Test
    @AllureId("2289646")
    @DisplayName("Получение ошибки апи в Compose OneTap смене аккаунта")
    override fun failedApiCallIsReceived() {
        super.failedApiCallIsReceived()
    }

    @Test
    @AllureId("2289858")
    @DisplayName("Получение ошибки отмены авторизации в Compose OneTap смене аккаунта")
    override fun cancellationIsReceived() {
        super.cancellationIsReceived()
    }

    @Test
    @AllureId("2289594")
    @DisplayName("Получение ошибки отсутствия данных oauth в Compose OneTap смене аккаунта")
    override fun failedOAuthIsReceived() {
        super.failedOAuthIsReceived()
    }

    @Test
    @AllureId("2289981")
    @DisplayName("Получение ошибки отсутствия deviceId в Compose OneTap смене аккаунта")
    override fun invalidDeviceIdIsReceived() {
        super.invalidDeviceIdIsReceived()
    }

    @Test
    @AllureId("2289713")
    @DisplayName("Получение ошибки неверного state в Compose OneTap смене аккаунта")
    override fun invalidStateIsReceived() {
        super.invalidStateIsReceived()
    }

    @Test
    @AllureId("2303018")
    @DisplayName("Успешное получение кода при схеме с бекендом в XML OneTap Мультибрендинге")
    override fun authCodeIsReceived() {
        super.authCodeIsReceived()
    }

    @Test
    @AllureId("2302988")
    @DisplayName("Получение ошибки загрузки пользовательских данных в Compose OneTap")
    override fun failedUserCallIsReceived() {
        super.failedUserCallIsReceived()
    }

    override fun setOneTapContent(
        onFail: (OneTapOAuth?, VKIDAuthFail) -> Unit,
        onAuthCode: (AuthCodeData, Boolean) -> Unit,
        onAuth: (OneTapOAuth?, AccessToken) -> Unit,
        authParams: VKIDAuthUiParams,
    ) {
        composeTestRule.setContent {
            OneTap(
                onAuth = onAuth,
                onAuthCode = onAuthCode,
                onFail = onFail,
                signInAnotherAccountButtonEnabled = true,
                authParams = authParams,
            )
        }
    }
}
