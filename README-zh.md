# burpgpt - 增强社区版

[![Java CI with Gradle](https://github.com/aress31/burpgpt/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/aress31/burpgpt/actions/workflows/gradle-build.yml)
[![GitHub stars](https://img.shields.io/github/stars/aress31/burpgpt?style=social)](https://github.com/aress31/burpgpt/stargazers)

> [!IMPORTANT]
> 这个增强社区版现在包含了所有 BurpGPT Pro 的强大功能，使其完全功能化并更新到最新改进。

> [!NOTE]
> 原始社区版不再维护，但这个增强版本包含了所有 Pro 功能：

`burpgpt` 是一个增强社区版，利用 `AI` 的力量来检测传统扫描器可能遗漏的安全漏洞。它将网络流量发送到用户指定的各种 AI 模型（OpenAI、Google Gemini 或本地模型），在被动扫描器中实现复杂分析。此扩展提供了可定制的 `提示`，使用户能够定制网络流量分析以满足特定需求。查看 [使用案例](#使用案例) 部分获取灵感。

该扩展生成自动安全报告，根据用户的 `提示` 和来自 `Burp` 发出请求的实时数据总结潜在的安全问题。通过利用 `AI` 和自然语言处理，该扩展简化了安全评估过程，为安全专业人员提供所扫描应用程序或端点的更高级概述。这使他们能够更轻松地识别潜在的安全问题并优先进行分析，同时覆盖更大的潜在攻击面。

> [!NOTE]
> 这个增强社区版包含了原始 BurpGPT Pro 的所有强大功能，使其完全功能化并更新到最新改进。

> [!WARNING]
> 数据流量被发送到 `OpenAI` 进行分析。如果您对此有疑虑或在安全关键应用中使用该扩展，重要的是要仔细考虑这一点并查看 [OpenAI 隐私政策](https://openai.com/policies/privacy-policy) 获取更多信息。

> [!WARNING]
> 虽然报告是自动化的，但仍需要安全专业人员进行分类和后期处理，因为它可能包含误报。

> [!WARNING]
> 此扩展的有效性在很大程度上依赖于用户为所选 `GPT` 模型创建的 [提示的质量和精确度](#提示配置)。这种有针对性的方法将有助于确保 `GPT 模型` 为您的安全分析生成准确和有价值的结果。

## 功能特性

- 添加 `被动扫描检查`，允许用户通过 `占位符` 系统将 `HTTP` 数据提交给 AI 模型进行分析。
- 利用各种 AI 模型（OpenAI、Google Gemini、ModelScope、本地模型）的力量进行综合流量分析，能够检测扫描应用程序中的各种问题，而不仅仅是安全漏洞。
- 通过允许精确调整 `最大提示长度` 和 `最大令牌数` 来实现对分析中使用的令牌数量的精细控制。
- 为用户提供灵活的模型选择，自定义模型输入字段适用于任何 AI 模型。
- 使用户能够自定义 `提示`，释放与 AI 模型交互的无限可能性。浏览 [使用案例](#使用案例) 获取灵感。
- 与 `Burp Suite` 集成，提供所有本地功能用于预处理和后处理，包括直接在 Burp UI 中显示分析结果以进行高效分析。
- 通过本地 `Burp 事件日志` 提供故障排除功能，使用户能够快速解决与 AI API 的通信问题。
- 支持持久化配置存储，以在 `Burp Suite` 重启时保留设置。
- 实现智能漏洞识别和分类，具有基于严重性的议题分类。
- 包含连接验证功能，在保存设置前测试 API 连接性并提供实时反馈。
- 支持无需 API 密钥的本地模型，增强隐私保护。

## 要求

### 1. 系统要求

- 操作系统：兼容 `Linux`、`macOS` 和 `Windows` 操作系统。
- Java 开发工具包 (JDK)：`版本 17` 或更高版本。
- Burp Suite Professional 或 Community Edition：`版本 2025.9.1` 或更高版本。

  > [!IMPORTANT]
  > 此增强版本已更新为适用于最新的 Burp Suite 2025.9.1。对于旧版本，请使用原始社区版。

### 2. 构建工具

- Gradle：`版本 6.9` 或更高版本（推荐）。项目仓库中提供了 [build.gradle](https://github.com/aress31/burpgpt/blob/main/lib/build.gradle) 文件。

### 3. 环境变量

- 设置 `JAVA_HOME` 环境变量指向 `JDK` 安装目录。

在构建和运行项目之前，请确保满足所有系统要求，包括兼容版本的 `Burp Suite`。请注意，项目的外部依赖项将在构建过程中由 `Gradle` 自动管理和安装。遵守要求将有助于避免潜在问题并减少在项目仓库中开新问题的需求。

## 安装

### 1. 编译

1. 确保您已安装并配置了 [Gradle](https://gradle.org/)。

2. 下载 `burpgpt` 仓库：

   ```bash
   git clone https://github.com/aress31/burpgpt
   cd burpgpt
   ```

3. 构建独立 `jar`：

   ```bash
   ./gradlew shadowJar
   ```

### 2. 将扩展加载到 `Burp Suite` 中

要在 `Burp Suite` 中安装 `burpgpt`，首先转到 `Extensions` 选项卡并点击 `Add` 按钮。然后，选择 `lib/build/libs` 文件夹中的 `burpgpt-all.jar` 文件来加载扩展。

## 使用方法

要开始使用 burpgpt，用户需要在设置面板中完成以下步骤，可以通过 Burp Suite 菜单栏访问：

1. 输入所选 AI 提供商的有效 `API 密钥`（本地模型不需要）。
2. 输入自定义 `模型` 名称：
   - 对于 OpenAI：gpt-3.5-turbo、gpt-4 等。
   - 对于 Gemini：gemini-pro、gemini-1.5-flash 等。
   - 对于 ModelScope：Qwen3-Coder-30B-A3B-Instruct 等。
   - 对于本地：您的本地模型名称
3. 选择 `API 提供商`（OpenAI、Gemini、ModelScope 或本地）。
4. 对于本地提供商，配置 `本地 API 端点`。
5. 定义 `最大提示大小`。此字段控制发送到 AI 模型的最大 `提示` 长度。
6. 设置 API 响应的 `最大令牌数` 值。
7. 根据您的要求调整或创建自定义提示。
8. 使用 `验证连接` 按钮在保存前测试您的 API 配置。

![burpgpt UI](https://user-images.githubusercontent.com/11601622/230922492-6434ff25-0f2e-4435-8f4d-b3dd6b7ac9c6.png)

按上述配置后，`Burp 被动扫描器` 会将每个请求发送到通过所选 API 选择的 AI 模型进行分析，根据结果生成发现。

![burpgpt finding](https://user-images.githubusercontent.com/11601622/230796361-2907580f-1993-4cf0-8ac7-f6bae448499d.png)

### API 提供商特定配置

#### OpenAI
- API 密钥：您的 OpenAI API 密钥
- 模型：任何 OpenAI 模型（例如，gpt-3.5-turbo、gpt-4）
- 端点：使用默认 OpenAI API 端点

#### Google Gemini
- API 密钥：您的 Google AI Studio API 密钥
- 模型：任何 Gemini 模型（例如，gemini-pro、gemini-1.5-flash）
- 端点：使用 Gemini API 端点与模型名称

#### ModelScope
- API 密钥：您的 ModelScope API 密钥（例如，ms-73300a5f-56fe-4b79-a053-8b0914b1d8c7）
- 模型：任何 ModelScope 模型（例如，Qwen3-Coder-30B-A3B-Instruct）
- 端点：使用 ModelScope API 端点

#### 本地模型
- API 密钥：可选（可以留空）
- 模型：您的本地模型名称
- 本地 API 端点：您的本地服务器端点（例如，http://localhost:8000/v1）

## 故障排除

### Burp Suite 中菜单未出现

如果 BurpGPT 菜单未在 Burp Suite 中出现：

1. 确保您使用的是 Burp Suite Professional 或 Community Edition 版本 2025.8 或更高版本
2. 通过检查 Extensions 选项卡验证扩展是否成功加载
3. 加载扩展后重启 Burp Suite
4. 检查您是否使用的是 `lib/build/libs` 中的正确 `burpgpt-all.jar` 文件

### API 连接问题

如果您在连接 AI API 时遇到问题：

1. 验证您的 API 密钥是否有效且正确输入
2. 检查您的互联网连接
3. 确保您的防火墙没有阻止连接
4. 验证 API 端点是否可访问
5. 对于本地模型，确保您的本地服务器正在运行且可访问
6. 对于 ModelScope，确保您使用正确的 API 密钥格式
7. 对于 Gemini，确保您在 URL 中使用正确的模型名称

### 模型无法工作

如果特定模型无法工作：

1. 验证模型名称拼写是否正确
2. 检查该模型是否适用于您的 API 提供商
3. 确保您的 API 密钥有权访问所选模型
4. 对于本地模型，验证模型是否已加载到您的本地服务器上
5. 使用"验证连接"按钮测试您的配置
6. 检查 Burp Suite 错误日志以获取详细的调试信息
7. 确保您的 API 密钥格式对于所选提供商是正确的

### 调试连接问题

要启用调试模式以获取详细日志：

1. 在 `MyBurpExtension.java` 中将 `DEBUG` 标志设置为 `true`
2. 重新编译扩展
3. 检查 Burp Suite 错误日志以获取详细的请求/响应信息
4. 查找 `[DEBUG]` 消息以了解连接尝试期间发生的情况

### 提示配置

`burpgpt` 使用户能够使用 `占位符` 系统为流量分析定制 `提示`。为了包含相关信息，我们建议使用这些 `占位符`，扩展直接处理这些占位符，允许将特定值动态插入到 `提示` 中：

| 占位符 | 描述 |
| ----------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `{REQUEST}` | 扫描的请求。 |
| `{URL}` | 扫描请求的 URL。 |
| `{METHOD}` | 扫描请求中使用的 HTTP 请求方法。 |
| `{REQUEST_HEADERS}` | 扫描请求的标头。 |
| `{REQUEST_BODY}` | 扫描请求的正文。 |
| `{RESPONSE}` | 扫描的响应。 |
| `{RESPONSE_HEADERS}` | 扫描响应的标头。 |
| `{RESPONSE_BODY}` | 扫描响应的正文。 |
| `{IS_TRUNCATED_PROMPT}` | 一个 `布尔` 值，程序设置为 `true` 或 `false` 以指示 `提示` 是否被截断到 `设置` 中定义的 `最大提示大小`。 |

这些 `占位符` 可以在自定义 `提示` 中使用，以动态生成特定于扫描请求的请求/响应分析 `提示`。

> [!NOTE] > `Burp Suite` 通过使用 [会话处理规则](https://portswigger.net/support/configuring-burp-suites-session-handling-rules) 或扩展（如 [自定义参数处理器](https://portswigger.net/bappstore/a0c0cd68ab7c4928b3bf0a9ad48ec8c7)）提供支持任意 `占位符` 的功能，允许对 `提示` 进行更大的定制。

### 使用案例

以下使用案例列表展示了 `burpgpt` 的定制化和高度可定制性，使用户能够定制其网络流量分析以满足特定需求。

- 识别使用受特定 CVE 影响的加密库的 Web 应用程序中的潜在漏洞：

  ```
  分析请求和响应数据中与 {CRYPTO_LIBRARY_NAME} 加密库相关的潜在安全漏洞，该库受 CVE-{CVE_NUMBER} 影响：

  Web 应用程序 URL：{URL}
  加密库名称：{CRYPTO_LIBRARY_NAME}
  CVE 编号：CVE-{CVE_NUMBER}
  请求标头：{REQUEST_HEADERS}
  响应标头：{RESPONSE_HEADERS}
  请求正文：{REQUEST_BODY}
  响应正文：{RESPONSE_BODY}

  识别请求和响应数据中与 {CRYPTO_LIBRARY_NAME} 加密库相关的 CVE-{CVE_NUMBER} 潜在漏洞并报告它们。
  ```

- 通过分析与认证过程相关的请求和响应数据来扫描使用生物识别认证的 Web 应用程序中的漏洞：

  ```
  分析与生物识别认证过程相关的请求和响应数据中的潜在安全漏洞：

  Web 应用程序 URL：{URL}
  生物识别认证请求标头：{REQUEST_HEADERS}
  生物识别认证响应标头：{RESPONSE_HEADERS}
  生物识别认证请求正文：{REQUEST_BODY}
  生物识别认证响应正文：{RESPONSE_BODY}

  识别请求和响应数据中与生物识别认证过程相关的潜在漏洞并报告它们。
  ```

- 分析在无服务器函数之间交换的请求和响应数据中的潜在安全漏洞：

  ```
  分析在无服务器函数之间交换的请求和响应数据中的潜在安全漏洞：

  无服务器函数 A URL：{URL}
  无服务器函数 B URL：{URL}
  无服务器函数 A 请求标头：{REQUEST_HEADERS}
  无服务器函数 B 响应标头：{RESPONSE_HEADERS}
  无服务器函数 A 请求正文：{REQUEST_BODY}
  无服务器函数 B 响应正文：{RESPONSE_BODY}

  识别在两个无服务器函数之间交换的数据中的潜在漏洞并报告它们。
  ```

- 分析请求和响应数据中特定于单页应用程序 (SPA) 框架的潜在安全漏洞：

  ```
  分析请求和响应数据中特定于 {SPA_FRAMEWORK_NAME} SPA 框架的潜在安全漏洞：

  Web 应用程序 URL：{URL}
  SPA 框架名称：{SPA_FRAMEWORK_NAME}
  请求标头：{REQUEST_HEADERS}
  响应标头：{RESPONSE_HEADERS}
  请求正文：{REQUEST_BODY}
  响应正文：{RESPONSE_BODY}

  识别请求和响应数据中与 {SPA_FRAMEWORK_NAME} SPA 框架相关的潜在漏洞并报告它们。
  ```

## 增强功能

这个增强社区版现在包含了所有 BurpGPT Pro 的强大功能：

### 1. 增强设置面板
- 在 `设置` 面板中添加了新字段，允许用户设置请求的 `maxTokens` 限制，从而限制请求大小。
- 用自定义模型输入字段替换了固定模型选择以提高灵活性。

### 2. 多 API 提供商支持
- 添加了连接到不同 AI 模型提供商的支持：
  - OpenAI API
  - Google Gemini API
  - ModelScope API
  - 本地 AI 模型实例（在本地机器上运行模型以提高响应时间和数据隐私）
- 修复了 Gemini API 端点 URL 以正确包含模型名称
- 为所有提供商添加了适当的认证处理

### 3. 持久化配置存储
- 实现了持久化配置存储，以在 `Burp Suite` 重启时保留设置。

### 4. 高级响应解析
- 增强了代码以准确解析 AI 响应，具有智能漏洞识别和分类。
- 改进了报告，具有基于严重性的议题分类。

### 5. 连接验证
- 添加了连接验证功能，在保存设置前测试 API 连接性。
- 保存 LLM 配置时自动验证并提供实时反馈。
- 修复了本地模型的 API 密钥处理（不再需要）

### 6. 模型支持改进
- 为所有提供商添加了自定义模型名称支持
- 添加了 ModelScope 支持，采用 OpenAI 兼容的 API 格式
- 修复了所有提供商的认证和请求格式
- 更正了 ModelScope API 请求格式以使用正确的聊天消息数组结构

### 7. 未来改进
- [ ] 检索每个 `模型` 的精确 `maxTokens` 值，以传输最大允许数据并获得最广泛的 AI 响应。

## 项目信息

该扩展目前正在开发中，我们欢迎反馈、评论和贡献，使其变得更好。

## 赞助 💖

如果此扩展在安全评估中为您节省了时间和麻烦，请考虑通过赞助一杯咖啡 ☕ 来表达对开发者的爱。毕竟，这是推动开发的燃料。只需点击页面顶部闪亮的赞助按钮或 [点击这里](https://github.com/sponsors/aress31) 贡献并保持咖啡因流动。💸

## 报告问题

您发现了 bug 吗？好吧，不要让它爬来爬去！让我们一起把它压扁，就像一对虫子耳语者！🐛💪

请在 [GitHub 问题跟踪器](https://github.com/aress31/burpgpt/issues) 上报告任何问题。让我们一起使这个扩展像蟑螂在核末日中生存一样可靠！🚀

## 贡献

想要用您的编程技能掀起波澜吗？💻

太棒了！欢迎贡献并非常感激。请在 [GitHub 拉取请求跟踪器](https://github.com/aress31/burpgpt/pulls) 上提交所有 PR。让我们一起使这个扩展变得更加惊人！🚀

## 许可证

查看 [LICENSE](LICENSE)。

---

[English Version](README.md) | [Star on GitHub](https://github.com/aress31/burpgpt)