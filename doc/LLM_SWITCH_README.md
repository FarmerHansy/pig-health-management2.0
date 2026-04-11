# LLM开关说明

本项目支持本地Ollama大模型与OpenAPI（如DeepSeek）两种调用方式。通过配置开关决定使用哪一种，并且在代码层面严格限制调用。

## 1. 开关配置位置

配置文件: backend/src/main/resources/application.yml

关键配置:

- ai.provider.local-enabled
- ai.provider.openapi-enabled
- ai.provider.fallback-enabled
- ai.openapi.base-url
- ai.openapi.api-key
- ai.openapi.chat.model
- ai.openapi.chat.temperature
- ai.ollama.url
- ai.ollama.chat.options.model
- ai.ollama.embedding.options.model

## 2. 常见场景配置

### 仅使用本地Ollama

```
ai:
  provider:
    local-enabled: true
    openapi-enabled: false
    fallback-enabled: false
  ollama:
    url: http://127.0.0.1:11434
    chat:
      options:
        model: qwen2.5:7b
    embedding:
      options:
        model: nomic-embed-text
```

### 仅使用OpenAPI（部署到云服务器推荐）

```
ai:
  provider:
    local-enabled: false
    openapi-enabled: true
    fallback-enabled: false
  openapi:
    base-url: https://api.deepseek.com
    api-key: ${DEEPSEEK_API_KEY:}
    chat:
      model: deepseek-chat
      temperature: 0.4
```

> 建议在服务器上通过环境变量设置API Key，例如:
> export DEEPSEEK_API_KEY=你的Key

### 本地优先 + 失败时切换OpenAPI

```
ai:
  provider:
    local-enabled: true
    openapi-enabled: true
    fallback-enabled: true
```

## 3. 代码层面的“严格开关”说明

- 当 ai.provider.local-enabled=false 时:
  - 不会创建Ollama相关Bean
  - 不会初始化知识库向量化
  - 不会调用本地模型或本地Embedding
- 当 ai.provider.openapi-enabled=false 时:
  - OpenAPI调用会直接报错
- 两者都关闭时:
  - 直接返回“AI服务未启用”异常

## 4. 调用接口说明

- /conversation/getOllama
  - 当本地开启时使用本地Ollama
  - 当本地关闭且OpenAPI开启时自动转为OpenAPI
- /conversation/getApiLLM
  - 仅在OpenAPI开启时可用
- /conversation/provider
  - 返回当前生效的提供方与模型信息

## 5. 注意事项

- 仅使用OpenAPI时，知识库检索会自动关闭（避免依赖本地Embedding）
- 本地Ollama部署地址与模型名需与实际一致
- OpenAPI Key务必通过环境变量或安全配置注入
