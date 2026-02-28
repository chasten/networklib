package com.example.networklib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * 基于固定 URL 拉取配置，并更新到 ConfigStore。
 */
class BootstrapConfigClient(
    private val bootstrapUrl: String,
    private val okHttpClient: OkHttpClient,
    private val configStore: ConfigStore,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    suspend fun refresh(): ServerConfig = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(bootstrapUrl)
            .get()
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            require(response.isSuccessful) {
                "Bootstrap config request failed: ${response.code}"
            }

            val raw = response.body?.string().orEmpty()
            val payload = json.decodeFromString<BootstrapConfigResponse>(raw)
            val config = ServerConfig(
                endpointMap = payload.endpoints,
                token = payload.token,
                cookie = payload.cookie
            )
            configStore.update(config)
            config
        }
    }
}
