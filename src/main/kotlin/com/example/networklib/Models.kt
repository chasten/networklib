package com.example.networklib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 固定配置接口返回的数据结构：
 * 1) endpoints: 按业务标识映射到真实 URL。
 * 2) token / cookie: 用于后续请求校验。
 */
@Serializable
data class BootstrapConfigResponse(
    @SerialName("endpoints") val endpoints: Map<String, String>,
    @SerialName("token") val token: String? = null,
    @SerialName("cookie") val cookie: String? = null
)

/**
 * 客户端内存中的配置模型。
 */
data class ServerConfig(
    val endpointMap: Map<String, String>,
    val token: String? = null,
    val cookie: String? = null
)

/**
 * 给请求打业务标签，拦截器会据此动态切换 URL。
 */
data class EndpointKey(val value: String)
