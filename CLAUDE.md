# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BurpGPT is a Burp Suite extension that leverages OpenAI's GPT models to detect security vulnerabilities that traditional scanners might miss. It sends web traffic to a user-specified OpenAI model for analysis within the passive scanner.

## Code Architecture

The extension follows a modular architecture with these key components:

1. **Main Extension Class** (`MyBurpExtension`): Implements the BurpExtension interface and serves as the entry point. It registers the scan check and menu items.

2. **Scan Check** (`MyScanCheck`): Implements the passive audit functionality that sends HTTP requests/responses to the GPT client for analysis.

3. **GPT Client** (`GPTClient`): Handles communication with the OpenAI API, including request formatting, API calls, and response parsing.

4. **GUI Components**: Swing-based UI for configuration including settings, placeholders reference, and about views.

5. **Data Models**: GPTRequest and GPTResponse classes for handling API communication data structures.

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
│                   ├── http/          # HTTP client for OpenAI API
│                   └── gui/           # GUI components and views
├── gradle/                # Gradle wrapper files
├── gradlew                # Gradle wrapper script (Unix)
├── gradlew.bat            # Gradle wrapper script (Windows)
└── settings.gradle        # Gradle settings
```

## Dependencies
- Java 17+
- Burp Suite Montoya API
- OpenAI API (OkHttp for HTTP client)
- Gson for JSON parsing
- Apache Commons Text for string utilities
- Lombok for reducing boilerplate code

## Loading in Burp Suite
1. Build the JAR file using `./gradlew shadowJar`
2. In Burp Suite, go to Extensions tab
3. Click "Add" button
4. Select the generated JAR file from `lib/build/libs/`