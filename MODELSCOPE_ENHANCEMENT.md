# BurpGPT ModelScope API 支持增强

## 问题描述
ModelScope API 请求格式不正确，导致无法与 ModelScope API 正常通信。ModelScope 使用与 OpenAI 兼容的聊天格式，但之前的实现使用了错误的请求体结构。

## 修复内容

### 1. 修复 ModelScope 请求体格式
- 使用聊天格式（chat format）而不是补全格式（completion format）
- 正确构建 messages 数组，包含 role 和 content 字段
- 添加了 temperature 和 max_tokens 参数

### 2. 修复了两个关键位置的 ModelScope 请求格式
- 连接验证功能中的请求体构建（验证测试）
- 实际 API 调用中的请求体构建（实际使用）

## 技术细节

### 之前的错误格式
```json
{
  "prompt": "prompt-content",
  "max_tokens": 10,
  "model": "model-name"
}
```

### 修复后的正确格式
```json
{
  "model": "model-name",
  "messages": [
    {
      "role": "user",
      "content": "prompt-content"
    }
  ],
  "temperature": 0.7,
  "max_tokens": 2048
}
```

## 验证
- 项目成功编译，没有错误
- 修复后的代码符合 ModelScope API 文档要求
- 更新了 CHANGELOG.md 记录此次修复

## API 端点确认
ModelScope API 端点配置正确：
`https://api-inference.modelscope.cn/v1/chat/completions`

## 配置说明
根据 README.md 中的说明，ModelScope 配置如下：
- API Key: ModelScope API 密钥 (例如: ms-73300a5f-56fe-4b79-a053-8b0914b1d8c7)
- Model: 任何 ModelScope 模型 (例如: Qwen3-Coder-30B-A3B-Instruct)
- Endpoint: 使用 ModelScope API 端点