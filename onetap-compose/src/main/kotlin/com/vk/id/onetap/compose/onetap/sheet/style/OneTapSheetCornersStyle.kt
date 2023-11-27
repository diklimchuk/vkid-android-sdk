package com.vk.id.onetap.compose.onetap.sheet.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

public sealed class OneTapSheetCornersStyle(
    public val radiusDp: Float
) {
    public object Default : OneTapSheetCornersStyle(ROUNDED_RADIUS_DP)
    public object None : OneTapSheetCornersStyle(NONE_RADIUS_DP)
    public object Rounded : OneTapSheetCornersStyle(ROUNDED_RADIUS_DP)
    public class Custom(radiusDp: Float) : OneTapSheetCornersStyle(radiusDp)

    private companion object {
        private const val ROUNDED_RADIUS_DP = 12f
        private const val NONE_RADIUS_DP = 0f
    }
}

internal fun Modifier.clip(style: OneTapSheetCornersStyle): Modifier {
    return clip(RoundedCornerShape(style.radiusDp.dp))
}