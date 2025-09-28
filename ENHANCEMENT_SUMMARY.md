# BurpGPT ModelScope API 支持增强

## 问题描述
ModelScope API 请求格式不正确，导致无法与 ModelScope API 正常通信。

## 修复内容
1. 修复了 ModelScope API 的请求体格式：
   - 使用聊天格式（chat format）而不是补全格式（completion format）
   - 正确构建 messages 数组，包含 role 和 content 字段
   - 添加了 temperature 和 max_tokens 参数

2. 修复了两个关键位置的 ModelScope 请求格式：
   - 连接验证功能中的请求体构建
   - 实际 API 调用中的请求体构建

## 技术细节
ModelScope API 使用与 OpenAI 兼容的聊天格式，需要以下结构：
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