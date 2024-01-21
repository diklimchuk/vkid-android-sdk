package com.vk.id.sample.app.uikit.selector

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
internal fun CheckboxSelector(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
    )
}
