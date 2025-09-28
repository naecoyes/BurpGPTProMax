# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added
- Connection validation feature with real-time feedback
- Custom model input field replacing fixed model selection
- Enhanced API provider support with validation
- ModelScope API support
- Support for local models without API keys
- Detailed API provider configuration documentation
- Comprehensive troubleshooting guide
- Enhanced debugging capabilities with detailed logging
- Chinese README documentation (README-zh.md)
- GitHub star tracking with automated badge updates
- Multi-language documentation support

### Changed
- Updated README with new features and usage instructions
- Improved Settings UI with validation button
- Updated build configuration for latest dependencies
- Enhanced documentation with API provider specific configuration
- Added detailed troubleshooting sections for all providers
- Added debugging instructions for connection issues

### Fixed
- Model selection now supports custom model names
- API connection validation before saving settings
- Improved error handling for API requests
- Fixed Gemini API endpoint and authentication
- Added proper support for all API providers
- Fixed API key handling for local models
- Added detailed debug logging for connection validation
- Fixed ModelScope API request format to use proper chat messages array structure

## [1.0.0] - 2025-09-28

### Added
- Enhanced Settings Panel with maxTokens field
- Multi-API Provider Support (OpenAI, Gemini, Local)
- Persistent Configuration Storage
- Advanced Response Parsing with vulnerability classification
- Compatibility with Burp Suite 2025.8

### Changed
- Updated Montoya API dependency to 2025.8
- Replaced fixed model selection with custom input
- Improved build process and documentation

[Unreleased]: https://github.com/aress31/burpgpt/compare/v1.0.0...HEAD