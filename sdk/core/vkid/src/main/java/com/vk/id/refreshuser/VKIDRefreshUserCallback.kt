package com.vk.id.refreshuser

import com.vk.id.VKIDUser

/**
 * Callback interface for refreshing user data.
 */
public interface VKIDRefreshUserCallback {
    /**
     * Called upon successful user refreshing.
     *
     * @param user Up-to-date user data.
     */
    public fun onSuccess(user: VKIDUser)

    /**
     * Called upon any failure during user refreshing.
     *
     * @param fail Information about a failure.
     */
    public fun onFail(fail: VKIDRefreshUserFail)
}
