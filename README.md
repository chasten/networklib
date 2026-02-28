# networklib

一个面向 Android/Kotlin 的轻量网络请求库示例，特点：

- 固定 `bootstrapUrl` 拉取服务端下发配置（多个业务 URL + token/cookie）。
- 后续请求按业务 key 动态切换 URL。
- 仅维护一个 `OkHttpClient`，减少资源开销。
- 结构简洁、可扩展（可继续增加重试、日志、证书固定等能力）。

## 使用方式

```kotlin
import com.example.networklib.NetworkLib
import com.example.networklib.NetworkLib.Companion.endpoint
import okhttp3.Request

val networkLib = NetworkLib(
    bootstrapUrl = "https://config.example.com/bootstrap"
)

// 1) 先拉取配置
networkLib.refreshConfig()

// 2) 发起业务请求（url 可写占位域名，拦截器会替换）
val request = Request.Builder()
    .url("https://placeholder.local/user/profile")
    .endpoint("userService")
    .get()
    .build()

val response = networkLib.execute(request)
```

> `bootstrap` 接口返回 JSON 示例：

```json
{
  "endpoints": {
    "userService": "https://user-api.example.com/v1",
    "orderService": "https://order-api.example.com"
  },
  "token": "your-token",
  "cookie": "sid=abc123"
}
```

## 目录

- `Models.kt`: 配置模型、业务 endpoint 标签。
- `ConfigStore.kt`: 线程安全配置存储。
- `Interceptors.kt`: 动态 URL + 鉴权头注入。
- `BootstrapConfigClient.kt`: 固定 URL 配置拉取。
- `NetworkLib.kt`: 对外入口。
