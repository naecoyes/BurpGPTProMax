# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BurpGPT is a Burp Suite extension that leverages various AI models (OpenAI, Google Gemini, ModelScope, or local models) to detect security vulnerabilities that traditional scanners might miss. It sends web traffic to a user-specified AI model for analysis within the passive scanner.

The extension generates automated security reports that summarize potential security issues based on customizable prompts and real-time data from Burp-issued requests. This enables security professionals to identify potential security issues and prioritize their analysis while covering a larger potential attack surface.

## Code Architecture

The extension follows a modular architecture with these key components:

1. **Main Extension Class** (`MyBurpExtension`): Implements the BurpExtension interface and serves as the entry point. It registers the scan check and menu items, manages configuration persistence, and handles extension initialization.

2. **Scan Check** (`MyScanCheck`): Implements the passive audit functionality that sends HTTP requests/responses to the GPT client for analysis. It processes the AI responses and creates audit issues in Burp Suite.

3. **GPT Client** (`GPTClient`): Handles communication with various AI APIs (OpenAI, Gemini, ModelScope, Local), including request formatting, API calls, response parsing, and connection validation. It supports multiple API providers with different request/response formats.

4. **GUI Components**: Swing-based UI for configuration including settings, placeholders reference, and about views. The settings view allows users to configure API keys, models, providers, and prompts.

5. **Data Models**: GPTRequest and GPTResponse classes for handling API communication data structures. The GPTResponse class includes functionality to extract structured vulnerability information from AI responses.

## Common Development Commands

### Building the Project
```bash
./gradlew shadowJar
```
This creates a standalone JAR file in `lib/build/libs/` that can be loaded into Burp Suite.

### Running Tests
```bash
./gradlew test
```

### Cleaning Build Artifacts
```bash
./gradlew clean
```

## Project Structure
```
burpgpt/
├── lib/                    # Main extension code
│   ├── build.gradle       # Build configuration
│   └── src/
│       └── main/
│           └── java/
│               ├── burp/              # Main Burp extension classes
│               └── burpgpt/           # Extension-specific packages
│                   ├── gpt/           # GPT request/response models
│                   ├── http/          # HTTP client for AI APIs
│                   └── gui/           # GUI components and views
│                       └── views/     # Individual view components
├── gradle/                # Gradle wrapper files
├── gradlew                # Gradle wrapper script (Unix)
├── gradlew.bat            # Gradle wrapper script (Windows)
└── settings.gradle        # Gradle settings
```

## Dependencies
- Java 21+
- Burp Suite Montoya API
- OkHttp for HTTP client
- Gson for JSON parsing
- Apache Commons Text for string utilities
- Apache Commons Lang for utility functions
- Lombok for reducing boilerplate code

## Loading in Burp Suite
1. Build the JAR file using `./gradlew shadowJar`
2. In Burp Suite, go to Extensions tab
3. Click "Add" button
4. Select the generated JAR file from `lib/build/libs/`

## Configuration Options
The extension supports multiple AI providers:
- OpenAI (GPT models)
- Google Gemini
- ModelScope (Qwen models)
- Local models (custom endpoints)

Each provider has specific API requirements and formats that are handled in the GPTClient class.

## Key Features
- Passive scan check integration with Burp Suite
- Support for multiple AI providers and models
- Customizable prompts with placeholder system
- Configurable maximum prompt size and token limits
- Connection validation functionality
- Persistent configuration storage
- Structured vulnerability extraction from AI responses
- Severity-based issue classification