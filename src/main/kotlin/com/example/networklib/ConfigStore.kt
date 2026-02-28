package com.example.networklib

import java.util.concurrent.atomic.AtomicReference

/**
 * 线程安全的配置存储，统一管理 endpoint 与鉴权信息。
 */
class ConfigStore {
    private val state = AtomicReference(ServerConfig(emptyMap()))

    fun update(config: ServerConfig) {
        state.set(config)
    }

    fun snapshot(): ServerConfig = state.get()

    fun endpointOf(key: String): String? = state.get().endpointMap[key]
}
