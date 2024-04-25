package com.vk.id.internal.concurrent

import com.vk.id.common.InternalVKIDApi
import kotlinx.coroutines.CoroutineDispatcher

@InternalVKIDApi
public interface CoroutinesDispatchers {
    public val io: CoroutineDispatcher
}
