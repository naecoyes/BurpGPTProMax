> [!IMPORTANT]
> This enhanced community edition now includes all the powerful features of BurpGPT Pro, making it fully functional and up-to-date with the latest improvements.

> [!NOTE]
> The original community edition is no longer maintained, but this enhanced version includes all Pro features:

# burpgpt - Enhanced Community Edition

[![Java CI with Gradle](https://github.com/aress31/burpgpt/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/aress31/burpgpt/actions/workflows/gradle-build.yml)
[![GitHub stars](https://img.shields.io/github/stars/aress31/burpgpt?style=social)](https://github.com/aress31/burpgpt/stargazers)

`burpgpt` is an enhanced community edition that leverages the power of `AI` to detect security vulnerabilities that traditional scanners might miss. It sends web traffic to various AI models (OpenAI, Google Gemini, or local models) specified by the user, enabling sophisticated analysis within the passive scanner. This extension offers customizable `prompts` that enable tailored web traffic analysis to meet the specific needs of each user. Check out the [Example Use Cases](#example-use-cases) section for inspiration.

The extension generates an automated security report that summarizes potential security issues based on the user's `prompt` and real-time data from `Burp`-issued requests. By leveraging `AI` and natural language processing, the extension streamlines the security assessment process and provides security professionals with a higher-level overview of the scanned application or endpoint. This enables them to more easily identify potential security issues and prioritize their analysis, while also covering a larger potential attack surface.

> [!NOTE]
> This enhanced community edition includes all the powerful features of the original BurpGPT Pro, making it fully functional and up-to-date with the latest improvements.

> [!WARNING]
> Data traffic is sent to `OpenAI` for analysis. If you have concerns about this or are using the extension for security-critical applications, it is important to carefully consider this and review [OpenAI's Privacy Policy](https://openai.com/policies/privacy-policy) for further information.

> [!WARNING]
> While the report is automated, it still requires triaging and post-processing by security professionals, as it may contain false positives.

> [!WARNING]
> The effectiveness of this extension is heavily reliant on the [quality and precision of the prompts](#prompt-configuration) created by the user for the selected `GPT` model. This targeted approach will help ensure the `GPT model` generates accurate and valuable results for your security analysis.

## Features

- Adds a `passive scan check`, allowing users to submit `HTTP` data to an AI model for analysis through a `placeholder` system.
- Leverages the power of various AI models (OpenAI, Google Gemini, ModelScope, Local models) to conduct comprehensive traffic analysis, enabling detection of various issues beyond just security vulnerabilities in scanned applications.
- Enables granular control over the number of tokens used in the analysis by allowing for precise adjustments of the `maximum prompt length` and `maximum tokens`.
- Offers users flexible model selection with custom model input field for any AI model.
- Empowers users to customise `prompts` and unleash limitless possibilities for interacting with AI models. Browse through the [Example Use Cases](#example-use-cases) for inspiration.
- Integrates with `Burp Suite`, providing all native features for pre- and post-processing, including displaying analysis results directly within the Burp UI for efficient analysis.
- Provides troubleshooting functionality via the native `Burp Event Log`, enabling users to quickly resolve communication issues with the AI APIs.
- Supports persistent configuration storage to preserve settings across `Burp Suite` restarts.
- Implements intelligent vulnerability identification and classification with severity-based issue categorization.
- Includes connection validation feature to test API connectivity before saving settings with real-time feedback.
- Supports local models without requiring API keys for enhanced privacy.

## Requirements

### 1. System requirements

- Operating System: Compatible with `Linux`, `macOS`, and `Windows` operating systems.
- Java Development Kit (JDK): `Version 17` or later.
- Burp Suite Professional or Community Edition: `Version 2025.9.1` or later.

  > [!IMPORTANT]
  > This enhanced version has been updated to work with the latest Burp Suite 2025.9.1. For older versions, please use the original community edition.

### 2. Build tool

- Gradle: `Version 6.9` or later (recommended). The [build.gradle](https://github.com/aress31/burpgpt/blob/main/lib/build.gradle) file is provided in the project repository.

### 3. Environment variables

- Set up the `JAVA_HOME` environment variable to point to the `JDK` installation directory.

Please ensure that all system requirements, including a compatible version of `Burp Suite`, are met before building and running the project. Note that the project's external dependencies will be automatically managed and installed by `Gradle` during the build process. Adhering to the requirements will help avoid potential issues and reduce the need for opening new issues in the project repository.

## Installation

### 1. Compilation

1. Ensure you have [Gradle](https://gradle.org/) installed and configured.

2. Download the `burpgpt` repository:

   ```bash
   git clone https://github.com/aress31/burpgpt
   cd burpgpt
   ```

3. Build the standalone `jar`:

   ```bash
   ./gradlew shadowJar
   ```

### 2. Loading the Extension Into `Burp Suite`

To install `burpgpt` in `Burp Suite`, first go to the `Extensions` tab and click on the `Add` button. Then, select the `burpgpt-all.jar` file located in the `lib/build/libs` folder to load the extension.

## Usage

To start using burpgpt, users need to complete the following steps in the Settings panel, which can be accessed from the Burp Suite menu bar:

1. Enter a valid `API key` for your chosen AI provider (not required for Local models).
2. Enter a custom `model` name:
   - For OpenAI: gpt-3.5-turbo, gpt-4, etc.
   - For Gemini: gemini-pro, gemini-1.5-flash, etc.
   - For ModelScope: Qwen3-Coder-30B-A3B-Instruct, etc.
   - For Local: your local model name
3. Select an `API Provider` (OpenAI, Gemini, ModelScope, or Local).
4. For Local provider, configure the `Local API Endpoint`.
5. Define the `max prompt size`. This field controls the maximum `prompt` length sent to the AI model.
6. Set the `max tokens` value for API responses.
7. Adjust or create custom prompts according to your requirements.
8. Use the `Validate Connection` button to test your API configuration before saving.

<img src="https://user-images.githubusercontent.com/11601622/230922492-6434ff25-0f2e-4435-8f4d-b3dd6b7ac9c6.png" alt="burpgpt UI" width="75%" height="75%">

Once configured as outlined above, the `Burp passive scanner` sends each request to the chosen AI model via the selected API for analysis, producing findings based on the results.

<img src="https://user-images.githubusercontent.com/11601622/230796361-2907580f-1993-4cf0-8ac7-f6bae448499d.png" alt="burpgpt finding" width="75%" height="75%">

### API Provider Specific Configuration

#### OpenAI
- API Key: Your OpenAI API key
- Model: Any OpenAI model (e.g., gpt-3.5-turbo, gpt-4)
- Endpoint: Uses default OpenAI API endpoint

#### Google Gemini
- API Key: Your Google AI Studio API key
- Model: Any Gemini model (e.g., gemini-pro, gemini-1.5-flash)
- Endpoint: Uses Gemini API endpoint with model name

#### ModelScope
- API Key: Your ModelScope API key (e.g., ms-73300a5f-56fe-4b79-a053-8b0914b1d8c7)
- Model: Any ModelScope model (e.g., Qwen3-Coder-30B-A3B-Instruct)
- Endpoint: Uses ModelScope API endpoint

#### Local Models
- API Key: Optional (can be left empty)
- Model: Your local model name
- Local API Endpoint: Your local server endpoint (e.g., http://localhost:8000/v1)

## Troubleshooting

### Menu Not Appearing in Burp Suite

If the BurpGPT menu is not appearing in Burp Suite:

1. Ensure you are using Burp Suite Professional or Community Edition version 2025.8 or later
2. Verify that the extension was loaded successfully by checking the Extensions tab
3. Restart Burp Suite after loading the extension
4. Check that you are using the correct `burpgpt-all.jar` file from `lib/build/libs`

### API Connection Issues

If you're experiencing issues connecting to AI APIs:

1. Verify your API key is valid and correctly entered
2. Check your internet connection
3. Ensure your firewall is not blocking the connection
4. Verify the API endpoint is accessible
5. For local models, ensure your local server is running and accessible
6. For ModelScope, ensure you're using the correct API key format
7. For Gemini, ensure you're using the correct model name in the URL

### Model Not Working

If a specific model is not working:

1. Verify the model name is spelled correctly
2. Check that the model is available for your API provider
3. Ensure your API key has access to the selected model
4. For local models, verify the model is loaded on your local server
5. Use the "Validate Connection" button to test your configuration
6. Check the Burp Suite error log for detailed debug information
7. Ensure your API key format is correct for the selected provider

### Debugging Connection Issues

To enable debug mode for detailed logging:

1. Set the `DEBUG` flag to `true` in `MyBurpExtension.java`
2. Recompile the extension
3. Check the Burp Suite error log for detailed request/response information
4. Look for `[DEBUG]` messages to understand what's happening during connection attempts


### Prompt Configuration

`burpgpt` enables users to tailor the `prompt` for traffic analysis using a `placeholder` system. To include relevant information, we recommend using these `placeholders`, which the extension handles directly, allowing dynamic insertion of specific values into the `prompt`:

| Placeholder             | Description                                                                                                                                                                |
| ----------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `{REQUEST}`             | The scanned request.                                                                                                                                                       |
| `{URL}`                 | The URL of the scanned request.                                                                                                                                            |
| `{METHOD}`              | The HTTP request method used in the scanned request.                                                                                                                       |
| `{REQUEST_HEADERS}`     | The headers of the scanned request.                                                                                                                                        |
| `{REQUEST_BODY}`        | The body of the scanned request.                                                                                                                                           |
| `{RESPONSE}`            | The scanned response.                                                                                                                                                      |
| `{RESPONSE_HEADERS}`    | The headers of the scanned response.                                                                                                                                       |
| `{RESPONSE_BODY}`       | The body of the scanned response.                                                                                                                                          |
| `{IS_TRUNCATED_PROMPT}` | A `boolean` value that is programmatically set to `true` or `false` to indicate whether the `prompt` was truncated to the `Maximum Prompt Size` defined in the `Settings`. |

These `placeholders` can be used in the custom `prompt` to dynamically generate a request/response analysis `prompt` that is specific to the scanned request.

> [!NOTE] > `Burp Suite` provides the capability to support arbitrary `placeholders` through the use of [Session handling rules](https://portswigger.net/support/configuring-burp-suites-session-handling-rules) or extensions such as [Custom Parameter Handler](https://portswigger.net/bappstore/a0c0cd68ab7c4928b3bf0a9ad48ec8c7), allowing for even greater customisation of the `prompts`.

### Example Use Cases

The following list of example use cases showcases the bespoke and highly customisable nature of `burpgpt`, which enables users to tailor their web traffic analysis to meet their specific needs.

- Identifying potential vulnerabilities in web applications that use a crypto library affected by a specific CVE:

  ```
  Analyse the request and response data for potential security vulnerabilities related to the {CRYPTO_LIBRARY_NAME} crypto library affected by CVE-{CVE_NUMBER}:

  Web Application URL: {URL}
  Crypto Library Name: {CRYPTO_LIBRARY_NAME}
  CVE Number: CVE-{CVE_NUMBER}
  Request Headers: {REQUEST_HEADERS}
  Response Headers: {RESPONSE_HEADERS}
  Request Body: {REQUEST_BODY}
  Response Body: {RESPONSE_BODY}

  Identify any potential vulnerabilities related to the {CRYPTO_LIBRARY_NAME} crypto library affected by CVE-{CVE_NUMBER} in the request and response data and report them.
  ```

- Scanning for vulnerabilities in web applications that use biometric authentication by analysing request and response data related to the authentication process:

  ```
  Analyse the request and response data for potential security vulnerabilities related to the biometric authentication process:

  Web Application URL: {URL}
  Biometric Authentication Request Headers: {REQUEST_HEADERS}
  Biometric Authentication Response Headers: {RESPONSE_HEADERS}
  Biometric Authentication Request Body: {REQUEST_BODY}
  Biometric Authentication Response Body: {RESPONSE_BODY}

  Identify any potential vulnerabilities related to the biometric authentication process in the request and response data and report them.
  ```

- Analysing the request and response data exchanged between serverless functions for potential security vulnerabilities:

  ```
  Analyse the request and response data exchanged between serverless functions for potential security vulnerabilities:

  Serverless Function A URL: {URL}
  Serverless Function B URL: {URL}
  Serverless Function A Request Headers: {REQUEST_HEADERS}
  Serverless Function B Response Headers: {RESPONSE_HEADERS}
  Serverless Function A Request Body: {REQUEST_BODY}
  Serverless Function B Response Body: {RESPONSE_BODY}

  Identify any potential vulnerabilities in the data exchanged between the two serverless functions and report them.
  ```

- Analysing the request and response data for potential security vulnerabilities specific to a Single-Page Application (SPA) framework:

  ```
  Analyse the request and response data for potential security vulnerabilities specific to the {SPA_FRAMEWORK_NAME} SPA framework:

  Web Application URL: {URL}
  SPA Framework Name: {SPA_FRAMEWORK_NAME}
  Request Headers: {REQUEST_HEADERS}
  Response Headers: {RESPONSE_HEADERS}
  Request Body: {REQUEST_BODY}
  Response Body: {RESPONSE_BODY}

  Identify any potential vulnerabilities related to the {SPA_FRAMEWORK_NAME} SPA framework in the request and response data and report them.
  ```

## Enhanced Features

This enhanced community edition now includes all the powerful features of BurpGPT Pro:

### 1. Enhanced Settings Panel
- Added a new field to the `Settings` panel that allows users to set the `maxTokens` limit for requests, thereby limiting the request size.
- Replaced fixed model selection with custom model input field for flexibility.

### 2. Multi-API Provider Support
- Added support for connecting to different AI model providers:
  - OpenAI API
  - Google Gemini API
  - ModelScope API
  - Local AI model instances (run models on your local machine for improved response times and data privacy)
- Fixed Gemini API endpoint URLs to properly include model names
- Added proper authentication handling for all providers

### 3. Persistent Configuration Storage
- Implemented persistent configuration storage to preserve settings across `Burp Suite` restarts.

### 4. Advanced Response Parsing
- Enhanced the code for accurate parsing of AI responses with intelligent vulnerability identification and classification.
- Improved reporting with severity-based issue categorization.

### 5. Connection Validation
- Added connection validation feature to test API connectivity before saving settings.
- Automatic validation when saving LLM configuration with real-time feedback.
- Fixed API key handling for local models (no longer required)

### 6. Model Support Improvements
- Added support for custom model names for all providers
- Added ModelScope support with OpenAI-compatible API format
- Fixed authentication and request formatting for all providers
- Corrected ModelScope API request format to use proper chat messages array structure

### 7. Future Improvements
- [ ] Retrieve the precise `maxTokens` value for each `model` to transmit the maximum allowable data and obtain the most extensive AI response possible.

## Project Information

The extension is currently under development and we welcome feedback, comments, and contributions to make it even better.

## Sponsor 💖

If this extension has saved you time and hassle during a security assessment, consider showing some love by sponsoring a cup of coffee ☕ for the developer. It's the fuel that powers development, after all. Just hit that shiny Sponsor button at the top of the page or [click here](https://github.com/sponsors/aress31) to contribute and keep the caffeine flowing. 💸

## Reporting Issues

Did you find a bug? Well, don't just let it crawl around! Let's squash it together like a couple of bug whisperers! 🐛💪

Please report any issues on the [GitHub issues tracker](https://github.com/aress31/burpgpt/issues). Together, we'll make this extension as reliable as a cockroach surviving a nuclear apocalypse! 🚀

## Contributing

Looking to make a splash with your mad coding skills? 💻

Awesome! Contributions are welcome and greatly appreciated. Please submit all PRs on the [GitHub pull requests tracker](https://github.com/aress31/burpgpt/pulls). Together we can make this extension even more amazing! 🚀

## License

See [LICENSE](LICENSE).

---

[中文版本](README-zh.md) | [Star on GitHub](https://github.com/aress31/burpgpt)
