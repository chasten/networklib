package com.example.networklib

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 统一注入 token/cookie，避免业务层重复拼接。
 */
class AuthInterceptor(
    private val configStore: ConfigStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val snapshot = configStore.snapshot()
        val builder = chain.request().newBuilder()

        snapshot.token?.takeIf { it.isNotBlank() }?.let {
            builder.header("Authorization", "Bearer $it")
        }
        snapshot.cookie?.takeIf { it.isNotBlank() }?.let {
            builder.header("Cookie", it)
        }

        return chain.proceed(builder.build())
    }
}

/**
 * 根据请求 tag(EndpointKey) 动态替换请求的 host/scheme/basePath。
 */
class DynamicUrlInterceptor(
    private val configStore: ConfigStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val endpointKey = request.tag(EndpointKey::class.java)?.value

        if (endpointKey.isNullOrBlank()) {
            return chain.proceed(request)
        }

        val targetBase = configStore.endpointOf(endpointKey)?.toHttpUrlOrNull()
            ?: return chain.proceed(request)

        val newUrl = rewriteUrl(targetBase, request.url)
        val newRequest = request.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }

    internal fun rewriteUrl(targetBase: HttpUrl, original: HttpUrl): HttpUrl {
        val basePath = targetBase.encodedPath.trimEnd('/')
        val requestPath = original.encodedPath.trimStart('/')
        val merged = buildString {
            append(if (basePath.isBlank()) "" else basePath)
            if (requestPath.isNotBlank()) {
                if (!endsWith('/')) append('/')
                append(requestPath)
            }
            if (isBlank()) append('/')
        }

        return targetBase.newBuilder()
            .encodedPath(merged)
            .encodedQuery(original.encodedQuery)
            .fragment(original.fragment)
            .build()
    }
}
