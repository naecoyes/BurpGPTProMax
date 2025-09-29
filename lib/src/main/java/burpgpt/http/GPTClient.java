package burpgpt.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import burp.MyBurpExtension;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;
import burpgpt.gpt.GPTRequest;
import burpgpt.gpt.GPTResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class GPTClient {

  private String apiKey;
  private String model;
  private int maxPromptSize;
  private int maxTokens; // Add maxTokens field
  private String apiProvider; // Add API provider field
  private String localApiEndpoint; // Add local API endpoint field
  private String prompt;
  private final OkHttpClient client;
  private final Gson gson;
  private Logging logging;

  public GPTClient(String apiKey, String model, String prompt, Logging logging) {
    this.apiKey = apiKey;
    this.model = model;
    this.maxTokens = 2048; // Default value
    this.apiProvider = "OpenAI"; // Default value
    this.localApiEndpoint = "http://localhost:8000/v1"; // Default value
    this.prompt = prompt;
    this.logging = logging;
    client = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build();
    gson = new Gson();
  }

  public void updateSettings(String newApiKey, String newModelId, int newMaxPromptSize, int newMaxTokens, String newPrompt) {
    this.apiKey = newApiKey;
    this.model = newModelId;
    this.maxPromptSize = newMaxPromptSize;
    this.maxTokens = newMaxTokens; // Update maxTokens
    this.prompt = newPrompt;
  }

  public void updateSettings(String newApiKey, String newModelId, int newMaxPromptSize, int newMaxTokens, String newApiProvider, String newLocalApiEndpoint, String newPrompt) {
    this.apiKey = newApiKey;
    this.model = newModelId;
    this.maxPromptSize = newMaxPromptSize;
    this.maxTokens = newMaxTokens; // Update maxTokens
    this.apiProvider = newApiProvider; // Update API provider
    this.localApiEndpoint = newLocalApiEndpoint; // Update local API endpoint
    this.prompt = newPrompt;
  }

  public boolean validateConnection() throws IOException {
    try {
      // Create a simple test request to validate the connection
      String testPrompt = "This is a test connection. Please respond with 'OK' if you receive this message.";

      // Get the appropriate API endpoint
      String apiEndpoint = getApiEndpoint();

      // Log the endpoint for debugging
      logging.logToOutput("[DEBUG] Validating connection to: " + apiEndpoint);

      // Create request body based on API provider
      JsonObject jsonObject = new JsonObject();

      switch (apiProvider) {
        case "OpenAI":
          jsonObject.addProperty("prompt", testPrompt);
          jsonObject.addProperty("max_tokens", 10);
          jsonObject.addProperty("model", model);
          break;

        case "Gemini":
          // For Gemini, we need to use the correct format
          JsonObject contents = new JsonObject();
          JsonObject part = new JsonObject();
          part.addProperty("text", testPrompt);
          contents.add("parts", part);
          jsonObject.add("contents", contents);
          JsonObject generationConfig = new JsonObject();
          generationConfig.addProperty("maxOutputTokens", 10);
          generationConfig.addProperty("temperature", 0.0);
          jsonObject.add("generationConfig", generationConfig);
          break;

        case "Local":
          jsonObject.addProperty("prompt", testPrompt);
          jsonObject.addProperty("max_tokens", 10);
          jsonObject.addProperty("model", model);
          break;

        case "ModelScope":
          // ModelScope uses chat format with messages array
          JsonObject message = new JsonObject();
          message.addProperty("role", "user");
          message.addProperty("content", testPrompt);

          // Create messages array and add the message
          com.google.gson.JsonArray messagesArray = new com.google.gson.JsonArray();
          messagesArray.add(message);

          // Add to main JSON object
          jsonObject.add("messages", messagesArray);
          jsonObject.addProperty("model", model);
          jsonObject.addProperty("temperature", 0.0);
          jsonObject.addProperty("max_tokens", 10);
          break;

        case "OpenRouter":
          // OpenRouter uses chat format with messages array (OpenAI compatible)
          JsonObject openRouterMessage = new JsonObject();
          openRouterMessage.addProperty("role", "user");
          openRouterMessage.addProperty("content", testPrompt);

          // Create messages array and add the message
          com.google.gson.JsonArray openRouterMessagesArray = new com.google.gson.JsonArray();
          openRouterMessagesArray.add(openRouterMessage);

          // Add to main JSON object
          jsonObject.add("messages", openRouterMessagesArray);
          jsonObject.addProperty("model", model);
          jsonObject.addProperty("temperature", 0.0);
          jsonObject.addProperty("max_tokens", 10);
          break;

        default:
          jsonObject.addProperty("prompt", testPrompt);
          jsonObject.addProperty("max_tokens", 10);
          jsonObject.addProperty("model", model);
          break;
      }

      String jsonBody = gson.toJson(jsonObject);
      logging.logToOutput("[DEBUG] Request body: " + jsonBody);

      MediaType JSON = MediaType.parse("application/json; charset=utf-8");
      RequestBody body = RequestBody.create(jsonBody, JSON);

      Request.Builder requestBuilder = new Request.Builder()
          .url(apiEndpoint)
          .addHeader("Content-Type", "application/json");

      // Add authorization header only if API key is provided and not empty
      // For local models, API key might not be required
      // For Gemini, API key is in URL parameter, not in Authorization header
      if (apiKey != null && !apiKey.isEmpty()) {
        String authHeader = getAuthorizationHeader(apiKey);
        if (authHeader != null) {
          logging.logToOutput("[DEBUG] Adding authorization header: " + authHeader);
          requestBuilder.addHeader("Authorization", authHeader);
        } else {
          logging.logToOutput("[DEBUG] Skipping authorization header for this provider");
        }
      }

      Request request = requestBuilder.post(body).build();

      try (Response response = client.newCall(request).execute()) {
        logging.logToOutput("[DEBUG] Response status: " + response.code());
        if (!response.isSuccessful()) {
          logging.logToOutput("[DEBUG] Response body: " + response.body().string());
        }
        return response.isSuccessful();
      }
    } catch (Exception e) {
      logging.logToError("Connection validation failed: " + e.getMessage());
      e.printStackTrace(); // Print stack trace for debugging
      return false;
    }
  }

  private String getApiEndpoint() {
    switch (apiProvider) {
      case "OpenAI":
        return "https://api.openai.com/v1/completions";
      case "Gemini":
        return "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;
      case "Local":
        return localApiEndpoint + "/completions";
      case "ModelScope":
        return "https://api-inference.modelscope.cn/v1/chat/completions";
      case "OpenRouter":
        return "https://openrouter.ai/api/v1/chat/completions";
      default:
        return "https://api.openai.com/v1/completions";
    }
  }

  private String getAuthorizationHeader(String apiKey) {
    switch (apiProvider) {
      case "OpenAI":
        return "Bearer " + apiKey;
      case "Gemini":
        // Gemini uses API key in URL parameter, not in Authorization header
        return null;
      case "Local":
        return "Bearer " + apiKey;
      case "ModelScope":
        return "Bearer " + apiKey;
      case "OpenRouter":
        return "Bearer " + apiKey;
      default:
        return "Bearer " + apiKey;
    }
  }

  private JsonObject createRequestBody(GPTRequest gptRequest, String model) {
    JsonObject jsonObject = new JsonObject();

    switch (apiProvider) {
      case "OpenAI":
        jsonObject.addProperty("prompt", gptRequest.getPrompt());
        jsonObject.addProperty("max_tokens", maxTokens);
        jsonObject.addProperty("n", gptRequest.getN());
        jsonObject.addProperty("model", model);
        break;

      case "Gemini":
        // Gemini uses a different request format
        JsonObject contents = new JsonObject();
        JsonObject part = new JsonObject();
        part.addProperty("text", gptRequest.getPrompt());
        contents.add("parts", part);
        jsonObject.add("contents", contents);
        // Add generation config
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("maxOutputTokens", maxTokens);
        generationConfig.addProperty("temperature", 0.7);
        generationConfig.addProperty("topP", 0.95);
        generationConfig.addProperty("topK", 40);
        jsonObject.add("generationConfig", generationConfig);
        break;

      case "Local":
        // Local API format (similar to OpenAI)
        jsonObject.addProperty("prompt", gptRequest.getPrompt());
        jsonObject.addProperty("max_tokens", maxTokens);
        jsonObject.addProperty("n", gptRequest.getN());
        jsonObject.addProperty("model", model);
        // Add temperature parameter for better control
        jsonObject.addProperty("temperature", 0.7);
        break;

      case "ModelScope":
        // ModelScope uses chat format with messages array
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", gptRequest.getPrompt());

        // Create messages array and add the message
        com.google.gson.JsonArray messagesArray = new com.google.gson.JsonArray();
        messagesArray.add(message);

        // Add to main JSON object
        jsonObject.add("messages", messagesArray);
        jsonObject.addProperty("model", model);
        jsonObject.addProperty("temperature", 0.7);
        jsonObject.addProperty("max_tokens", maxTokens);
        break;

      case "OpenRouter":
        // OpenRouter uses chat format with messages array (OpenAI compatible)
        JsonObject openRouterMessage = new JsonObject();
        openRouterMessage.addProperty("role", "user");
        openRouterMessage.addProperty("content", gptRequest.getPrompt());

        // Create messages array and add the message
        com.google.gson.JsonArray openRouterMessagesArray = new com.google.gson.JsonArray();
        openRouterMessagesArray.add(openRouterMessage);

        // Add to main JSON object
        jsonObject.add("messages", openRouterMessagesArray);
        jsonObject.addProperty("model", model);
        jsonObject.addProperty("temperature", 0.7);
        jsonObject.addProperty("max_tokens", maxTokens);
        break;

      default:
        jsonObject.addProperty("prompt", gptRequest.getPrompt());
        jsonObject.addProperty("max_tokens", maxTokens);
        jsonObject.addProperty("n", gptRequest.getN());
        jsonObject.addProperty("model", model);
        break;
    }

    return jsonObject;
  }

  public Pair<GPTRequest, GPTResponse> identifyVulnerabilities(HttpRequestResponse selectedMessage) throws IOException {
    HttpRequest selectedRequest = selectedMessage.request();
    HttpResponse selectedResponse = selectedMessage.response();

    // Always log the URL being analyzed
    logging.logToOutput("[+] Analyzing HTTP request: " + selectedRequest.url());

    if (MyBurpExtension.DEBUG) {
      logging.logToOutput("[*] Selected request:");
      logging.logToOutput(String.format("- url: %s\n" +
          "- method: %s\n" +
          "- headers: %s\n" +
          "- body: %s",
          selectedRequest.url(),
          selectedRequest.method(),
          selectedRequest.headers().toString(),
          selectedRequest.bodyToString()));

      logging.logToOutput("[*] Selected response:");
      logging.logToOutput(String.format("- headers: %s\n" +
          "- body: %s",
          selectedResponse.headers().toString(),
          selectedResponse.bodyToString()));
    }

    // This code sends the selected request/response information to ChatGPT
    // and receives a list of potential vulnerabilities in response.
    // TODO: Add a field to specify the maxTokens value
    try {
      GPTRequest gptRequest = new GPTRequest(selectedRequest, selectedResponse, model, 1, maxPromptSize);
      GPTResponse gptResponse = getCompletions(gptRequest, apiKey, model, prompt);
      return Pair.of(gptRequest, gptResponse);
    } catch (IOException e) {
      throw e;
    }
  }

  private GPTResponse getCompletions(GPTRequest gptRequest, String apiKey, String model, String prompt)
      throws IOException {
    gptRequest.setPrompt(prompt);

    String apiEndpoint = getApiEndpoint(); // Use dynamic API endpoint
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Create request body based on API provider
    JsonObject jsonObject = createRequestBody(gptRequest, model);
    String jsonBody = gson.toJson(jsonObject);

    RequestBody body = RequestBody.create(jsonBody, JSON);

    Request.Builder requestBuilder = new Request.Builder()
        .url(apiEndpoint)
        .addHeader("Content-Type", "application/json");

    // Add authorization header only if API key is provided and not empty
    // For local models, API key might not be required
    if (apiKey != null && !apiKey.isEmpty()) {
        requestBuilder.addHeader("Authorization", getAuthorizationHeader(apiKey));
    }

    Request request = requestBuilder.post(body).build();

    // Always log that we're sending a request to the LLM
    logging.logToOutput("[+] Sending request to LLM API: " + apiEndpoint);

    if (MyBurpExtension.DEBUG) {
      // Write the request body to a buffer
      Buffer buffer = new Buffer();
      request.body().writeTo(buffer);

      logging.logToOutput("[+] Completion request sent:");
      logging.logToOutput(String.format("- request: %s\n" +
          "- requestBody: %s", request, buffer.readUtf8()));
    }

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        handleErrorResponse(response);
      } else {
        String responseBody = response.body().string();

        // Always log that we received a response from the LLM
        logging.logToOutput("[+] Received response from LLM API with status: " + response.code());

        if (MyBurpExtension.DEBUG) {
          logging.logToOutput("[+] Completion response received:");
          logging.logToOutput(String.format("- responseBody: %s",
              responseBody));
        }

        // Parse response based on API provider
        return parseResponse(responseBody);
      }
    } catch (IOException e) {
      throw new IOException(e);
    }

    return null;
  }

  private void handleErrorResponse(Response response) throws IOException {
    int statusCode = response.code();
    String responseBody = response.body().string();

    switch (statusCode) {
      case 400:
        throw new IOException(String.format("Bad request (400): %s", responseBody));
      case 401:
        throw new IOException(String.format("Unauthorized (401): %s", responseBody));
      case 429:
        throw new IOException(String.format("Too many requests (429): %s", responseBody));
      default:
        throw new IOException(
            String.format("Unhandled response code (%d): %s", statusCode, responseBody));
    }
  }

  private GPTResponse parseResponse(String responseBody) {
    try {
      switch (apiProvider) {
        case "OpenAI":
          // OpenAI response format
          return gson.fromJson(responseBody, GPTResponse.class);

        case "Gemini":
          // Gemini response format - need to convert to GPTResponse format
          return convertGeminiResponseToGPTResponse(responseBody);

        case "Local":
          // Local API response format (assume similar to OpenAI)
          return gson.fromJson(responseBody, GPTResponse.class);

        default:
          // Default to OpenAI format
          return gson.fromJson(responseBody, GPTResponse.class);
      }
    } catch (Exception e) {
      logging.logToError(String.format("Error parsing response: %s", e.getMessage()));
      return null;
    }
  }

  private GPTResponse convertGeminiResponseToGPTResponse(String geminiResponseBody) {
    try {
      // Parse Gemini response
      JsonObject geminiResponse = gson.fromJson(geminiResponseBody, JsonObject.class);

      // Create GPTResponse object
      GPTResponse gptResponse = new GPTResponse(new ArrayList<>());

      // Extract text from Gemini response
      if (geminiResponse.has("candidates")) {
        // Get the first candidate
        JsonObject candidate = geminiResponse.getAsJsonArray("candidates").get(0).getAsJsonObject();
        if (candidate.has("content")) {
          JsonObject content = candidate.getAsJsonObject("content");
          if (content.has("parts")) {
            JsonObject part = content.getAsJsonArray("parts").get(0).getAsJsonObject();
            String text = part.get("text").getAsString();

            // Create a choice object
            GPTResponse.Choice choice = gptResponse.new Choice();
            choice.setText(text);
            choice.setIndex(0);
            choice.setFinishReason("stop");

            // Add to choices
            List<GPTResponse.Choice> choices = new ArrayList<>();
            choices.add(choice);
            gptResponse.setChoices(choices);
          }
        }
      }

      return gptResponse;
    } catch (Exception e) {
      logging.logToError(String.format("Error converting Gemini response: %s", e.getMessage()));
      return null;
    }
  }
}
