package burp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.ui.menu.Menu;
import burpgpt.gui.MyMenu;
import burpgpt.http.GPTClient;
import lombok.Getter;

public class MyBurpExtension implements BurpExtension, PropertyChangeListener {

    public static final Boolean DEBUG = false;

    public static final String EXTENSION = "BurpGPT Pro";
    public static final String VERSION = "0.1.1";

    private PropertyChangeSupport propertyChangeSupport;
    @Getter
    Logging logging;
    @Getter
    MontoyaApi montoyaApi;
    private PersistedObject persistedData;

    @Getter
    private String apiKey = "PLEASE_CHANGE_ME_OR_YOU_WILL_MAKE_THE_DEVELOPER_SAD";
    @Getter
    private int maxPromptSize = 1024;
    @Getter
    private int maxTokens = 2048; // Default maxTokens value
    @Getter
    private String apiProvider = "OpenAI"; // Default API provider
    @Getter
    private String localApiEndpoint = "http://localhost:8000/v1"; // Default local API endpoint
    @Getter
    private String model = "gpt-3.5-turbo"; // Default model
    @Getter
    String prompt = "Please analyze the following HTTP request and response for potential security vulnerabilities, "
            + "specifically focusing on OWASP top 10 vulnerabilities such as SQL injection, XSS, CSRF, and other common web application security threats.\n\n"
            + "Format your response as a bullet list with each point listing a vulnerability name and a brief description, in the format:\n"
            + "- Vulnerability Name: Brief description of vulnerability\n\n"
            + "Exclude irrelevant information.\n\n"
            + "=== Request ===\n"
            + "{REQUEST}\n\n"
            + "=== Response ===\n"
            + "{RESPONSE}\n";

    private GPTClient gptClient;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        this.montoyaApi = montoyaApi;
        this.logging = montoyaApi.logging();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.persistedData = montoyaApi.persistence().extensionData();

        montoyaApi.extension().setName(EXTENSION);
        logging.logToOutput("[+] Extension loaded");

        // Load saved configuration
        loadSavedConfiguration();

        gptClient = new GPTClient(apiKey, model, prompt, logging);
        MyScanCheck scanCheck = new MyScanCheck(gptClient, logging);

        Menu menu = MyMenu.createMenu(this);
        montoyaApi.userInterface().menuBar().registerMenu(menu);
        logging.logToOutput("[+] Menu added to the menu bar");

        montoyaApi.scanner().registerScanCheck(scanCheck);
        logging.logToOutput("[+] Passive scan check registered");
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    private void loadSavedConfiguration() {
        try {
            // Load API key
            String savedApiKey = persistedData.getString("apiKey");
            if (savedApiKey != null && !savedApiKey.isEmpty()) {
                this.apiKey = savedApiKey;
            }

            // Load model
            String savedModel = persistedData.getString("model");
            if (savedModel != null && !savedModel.isEmpty()) {
                this.model = savedModel;
            }

            // Load max prompt size
            Integer savedMaxPromptSize = persistedData.getInteger("maxPromptSize");
            if (savedMaxPromptSize != null) {
                this.maxPromptSize = savedMaxPromptSize;
            }

            // Load max tokens
            Integer savedMaxTokens = persistedData.getInteger("maxTokens");
            if (savedMaxTokens != null) {
                this.maxTokens = savedMaxTokens;
            }

            // Load API provider
            String savedApiProvider = persistedData.getString("apiProvider");
            if (savedApiProvider != null && !savedApiProvider.isEmpty()) {
                this.apiProvider = savedApiProvider;
            }

            // Load local API endpoint
            String savedLocalApiEndpoint = persistedData.getString("localApiEndpoint");
            if (savedLocalApiEndpoint != null && !savedLocalApiEndpoint.isEmpty()) {
                this.localApiEndpoint = savedLocalApiEndpoint;
            }

            // Load prompt
            String savedPrompt = persistedData.getString("prompt");
            if (savedPrompt != null && !savedPrompt.isEmpty()) {
                this.prompt = savedPrompt;
            }

            logging.logToOutput("[+] Configuration loaded from persistence");
        } catch (Exception e) {
            logging.logToError("[-] Error loading configuration: " + e.getMessage());
        }
    }

    private void saveConfiguration() {
        try {
            // Save API key
            persistedData.setString("apiKey", this.apiKey);

            // Save model
            persistedData.setString("model", this.model);

            // Save max prompt size
            persistedData.setInteger("maxPromptSize", this.maxPromptSize);

            // Save max tokens
            persistedData.setInteger("maxTokens", this.maxTokens);

            // Save API provider
            persistedData.setString("apiProvider", this.apiProvider);

            // Save local API endpoint
            persistedData.setString("localApiEndpoint", this.localApiEndpoint);

            // Save prompt
            persistedData.setString("prompt", this.prompt);

            logging.logToOutput("[+] Configuration saved to persistence");
        } catch (Exception e) {
            logging.logToError("[-] Error saving configuration: " + e.getMessage());
        }
    }

    public void updateSettings(String newApiKey, String newModelId, int newMaxPromptSize, int newMaxTokens, String newApiProvider, String newLocalApiEndpoint, String newPrompt) {
        String[] newValues = {
                newApiKey, newModelId, Integer.toString(newMaxPromptSize), Integer.toString(newMaxTokens), newApiProvider, newLocalApiEndpoint, newPrompt };
        String[] oldValues = {
                this.apiKey, this.model, Integer.toString(this.maxPromptSize), Integer.toString(this.maxTokens), this.apiProvider, this.localApiEndpoint, this.prompt };

        this.apiKey = newApiKey;
        this.model = newModelId;
        this.maxPromptSize = newMaxPromptSize;
        this.maxTokens = newMaxTokens;
        this.apiProvider = newApiProvider;
        this.localApiEndpoint = newLocalApiEndpoint;
        this.prompt = newPrompt;

        this.gptClient.updateSettings(newApiKey, newModelId, newMaxPromptSize, newMaxTokens, newPrompt);

        propertyChangeSupport.firePropertyChange("settingsChanged", oldValues, newValues);

        // Save configuration automatically
        saveConfiguration();

        if (MyBurpExtension.DEBUG) {
            logging.logToOutput("[*] Updated extension settings:");
            logging.logToOutput(String.format("- apiKey: %s\n" +
                    "- model: %s\n" +
                    "- maxPromptSize: %s\n" +
                    "- maxTokens: %s\n" +
                    "- apiProvider: %s\n" +
                    "- localApiEndpoint: %s\n" +
                    "- prompt: %s",
                    newApiKey, newModelId, newMaxPromptSize, newMaxTokens, newApiProvider, newLocalApiEndpoint, newPrompt));
        }
    }
}
