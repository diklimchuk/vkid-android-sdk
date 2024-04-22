package com.vk.id.sample.app.screen.styling

import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vk.id.AccessToken
import com.vk.id.auth.AuthCodeData
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.common.OneTapOAuth
import com.vk.id.onetap.common.OneTapStyle
import com.vk.id.onetap.common.button.style.OneTapButtonCornersStyle
import com.vk.id.onetap.common.button.style.OneTapButtonElevationStyle
import com.vk.id.onetap.common.button.style.OneTapButtonSizeStyle
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.xml.OneTap
import com.vk.id.sample.app.screen.UseToken
import com.vk.id.sample.app.uikit.selector.CheckboxSelector
import com.vk.id.sample.app.uikit.selector.DropdownSelector
import com.vk.id.sample.app.uikit.selector.EnumStateCheckboxSelector
import com.vk.id.sample.app.uikit.selector.SliderSelector
import com.vk.id.sample.app.uikit.selector.styleConstructors
import com.vk.id.sample.app.uikit.theme.AppTheme
import com.vk.id.sample.app.util.carrying.carry
import com.vk.id.sample.xml.uikit.common.dpToPixels
import com.vk.id.sample.xml.uikit.common.getOneTapFailCallback
import com.vk.id.sample.xml.uikit.common.getOneTapSuccessCallback
import com.vk.id.sample.xml.uikit.common.showToast
import com.vk.id.sample.xml.vkid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KCallable

private const val TOTAL_WIDTH_PADDING_DP = 16
private const val MIN_WIDTH_DP = 48f
private const val MAX_RADIUS_DP = 30
private const val MAX_ELEVATION_DP = 20

@Suppress("LongMethod")
@Composable
internal fun OnetapStylingComposeScreen() {
    val token = remember { mutableStateOf<AccessToken?>(null) }
    var code by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val screenWidth = LocalConfiguration.current.screenWidthDp - TOTAL_WIDTH_PADDING_DP

    val widthPercent = remember { mutableFloatStateOf(1f) }
    val cornersStylePercent = remember { mutableFloatStateOf(0f) }
    val selectedSize = remember { mutableStateOf(OneTapButtonSizeStyle.DEFAULT) }
    val selectedElevationStyle = remember { mutableFloatStateOf(0f) }
    val selectedOAuths = remember { mutableStateOf(emptySet<OneTapOAuth>()) }
    val styleConstructor = remember<MutableState<KCallable<OneTapStyle>?>> { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            styleConstructor.value = OneTapStyle.Companion::system.carry(context)
        }
    }
    val shouldUseXml = remember { mutableStateOf(false) }
    val signInToAnotherAccountEnabled = remember { mutableStateOf(true) }
    var state by remember { mutableStateOf("") }
    var codeChallenge by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(OneTapStyle.system(context)) }
    LaunchedEffect(
        styleConstructor.value to cornersStylePercent.floatValue,
        selectedSize.value to selectedElevationStyle.floatValue
    ) {
        withContext(Dispatchers.IO) {
            styleConstructor.value?.let {
                selectedStyle = it.call(
                    OneTapButtonCornersStyle.Custom(MAX_RADIUS_DP * cornersStylePercent.floatValue),
                    selectedSize.value,
                    OneTapButtonElevationStyle.Custom(MAX_ELEVATION_DP * selectedElevationStyle.floatValue),
                )
            }
        }
    }

    var styleConstructors by remember { mutableStateOf<Map<String, KCallable<OneTapStyle>>>(emptyMap()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            styleConstructors = OneTapStyle::class.styleConstructors(context)
        }
    }

    val useDarkTheme = selectedStyle is OneTapStyle.Dark ||
        selectedStyle is OneTapStyle.TransparentDark
    AppTheme(
        useDarkTheme = useDarkTheme
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val width = maxOf(MIN_WIDTH_DP, (screenWidth * widthPercent.floatValue))
                val onAuthCode = { data: AuthCodeData, isCompletion: Boolean ->
                    code = data.code
                    token.value = null
                    if (isCompletion) {
                        showToast(context, "Received auth code")
                    }
                }
                val authParams = VKIDAuthUiParams {
                    this.state = state.takeIf { it.isNotBlank() }
                    this.codeChallenge = codeChallenge.takeIf { it.isNotBlank() }
                }
                if (shouldUseXml.value) {
                    var oneTapView: OneTap? by remember { mutableStateOf(null) }
                    AndroidView(factory = { context ->
                        OneTap(context).apply {
                            setVKID(context.vkid)
                            setCallbacks(
                                onAuth = getOneTapSuccessCallback(context) { token.value = it },
                                onAuthCode = onAuthCode,
                                onFail = getOneTapFailCallback(context),
                            )
                            oneTapView = this
                        }
                    })
                    oneTapView?.apply {
                        this.layoutParams = LayoutParams(
                            if (selectedStyle is OneTapStyle.Icon) WRAP_CONTENT else context.dpToPixels(width.toInt()),
                            WRAP_CONTENT,
                        )
                        this.style = selectedStyle
                        this.oAuths = selectedOAuths.value
                        this.isSignInToAnotherAccountEnabled = signInToAnotherAccountEnabled.value
                        this.authParams = authParams
                    }
                } else {
                    OneTap(
                        modifier = Modifier.width(width.dp),
                        style = selectedStyle,
                        onAuth = getOneTapSuccessCallback(context) { token.value = it },
                        onAuthCode = onAuthCode,
                        onFail = getOneTapFailCallback(context),
                        oAuths = selectedOAuths.value,
                        signInAnotherAccountButtonEnabled = signInToAnotherAccountEnabled.value,
                        authParams = authParams,
                    )
                }
            }
            code?.let { value ->
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = { code = it },
                    label = { Text("Resulting auth code") },
                    maxLines = 1,
                )
            }
            token.value?.let {
                UseToken(accessToken = it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            CheckboxSelector(
                title = "XML",
                isChecked = shouldUseXml.value,
                onCheckedChange = { shouldUseXml.value = it }
            )
            CheckboxSelector(
                title = "Change account",
                isChecked = signInToAnotherAccountEnabled.value,
                onCheckedChange = { signInToAnotherAccountEnabled.value = it }
            )
            EnumStateCheckboxSelector(state = selectedOAuths)
            DropdownSelector(
                modifier = Modifier.padding(vertical = 16.dp),
                values = styleConstructors,
                selectedValue = selectedStyle::class.simpleName ?: error("Can get simple style"),
                onValueSelected = {
                    styleConstructor.value = it
                }
            )
            SliderSelector(title = "Width", selectedState = widthPercent)
            SliderSelector(title = "Corners", selectedState = cornersStylePercent)
            SliderSelector(title = "Elevation", selectedState = selectedElevationStyle)
            DropdownSelector(
                values = OneTapButtonSizeStyle.entries.associateBy { it.name },
                selectedValue = selectedSize.value.name,
                onValueSelected = { selectedSize.value = it }
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state,
                onValueChange = { state = it },
                label = { Text("State (Optional)") },
                shape = RectangleShape,
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = codeChallenge,
                onValueChange = { codeChallenge = it },
                label = { Text("Code challenge (Optional)") },
                shape = RectangleShape,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
