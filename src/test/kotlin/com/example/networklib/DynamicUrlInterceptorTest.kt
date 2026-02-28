package com.example.networklib

import kotlin.test.assertEquals
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test

class DynamicUrlInterceptorTest {
    @Test
    fun `rewriteUrl should merge base path and original path`() {
        val interceptor = DynamicUrlInterceptor(ConfigStore())

        val rewritten = interceptor.rewriteUrl(
            targetBase = "https://api.example.com/gateway".toHttpUrl(),
            original = "https://placeholder.local/order/list?page=1".toHttpUrl()
        )

        assertEquals(
            "https://api.example.com/gateway/order/list?page=1",
            rewritten.toString()
        )
    }
}
