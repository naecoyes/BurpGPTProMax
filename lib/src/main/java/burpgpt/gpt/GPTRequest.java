package burpgpt.gpt;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.Getter;

public class GPTRequest {
    @Getter
    private final int n;
    @Getter
    private String model;
    @Getter
    private final int maxPromptSize;
    @Getter
    private String prompt;
    private final String url;
    private final String method;
    private final String requestHeaders;
    private final String requestBody;
    private final String responseHeaders;
    private final String responseBody;

    private final String request;
    private final String response;

    public GPTRequest(HttpRequest httpRequest, HttpResponse httpResponse, String model, int n, int maxPromptSize) {
        this.url = httpRequest.url();
        this.method = httpRequest.method();
        this.requestHeaders = httpRequest.headers().toString();
        this.requestBody = httpRequest.bodyToString();
        this.responseHeaders = httpResponse.headers().toString();
        this.responseBody = httpResponse.bodyToString();

        this.request = httpRequest.toString();
        this.response = httpResponse.toString();

        this.model = model;
        this.n = n;
        this.maxPromptSize = maxPromptSize;
    }

    public void setPrompt(String prompt) {
        String[] placeholders = {
                "{REQUEST}", "{RESPONSE}", "{IS_TRUNCATED_PROMPT}",
                "{URL}", "{METHOD}", "{REQUEST_HEADERS}",
                "{REQUEST_BODY}", "{RESPONSE_HEADERS}", "{RESPONSE_BODY}" };
        String[] replacements = {
                request, response, Boolean.toString(prompt.length() > maxPromptSize),
                url, method, requestHeaders,
                requestBody, responseHeaders, responseBody };

        for (int i = 0; i < placeholders.length; i++) {
            prompt = prompt.replace(placeholders[i], replacements[i]);
        }

        if (prompt.length() > maxPromptSize) {
            prompt = prompt.substring(0, maxPromptSize);
        }

        this.prompt = prompt;
    }
}
