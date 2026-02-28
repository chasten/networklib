package com.example.networklib

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * 网络库入口：
 * - 只维护一个 OkHttpClient。
 * - 通过固定配置接口刷新 endpoint/token/cookie。
 * - 后续请求用 tag 标识业务域名，拦截器自动切换 URL。
 */
class NetworkLib(
    bootstrapUrl: String,
    okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
) {
    private val configStore = ConfigStore()

    private val client: OkHttpClient = okHttpBuilder
        .addInterceptor(DynamicUrlInterceptor(configStore))
        .addInterceptor(AuthInterceptor(configStore))
        .build()

    val bootstrapClient = BootstrapConfigClient(
        bootstrapUrl = bootstrapUrl,
        okHttpClient = client,
        configStore = configStore
    )

    suspend fun refreshConfig(): ServerConfig = bootstrapClient.refresh()

    fun execute(request: Request): Response = client.newCall(request).execute()

    companion object {
        /**
         * 给请求标记业务 key，动态 URL 拦截器将据此路由。
         */
        fun Request.Builder.endpoint(key: String): Request.Builder {
            return this.tag(EndpointKey::class.java, EndpointKey(key))
        }
    }
}
