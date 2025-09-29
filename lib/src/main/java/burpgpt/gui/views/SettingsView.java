package burpgpt.gui.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import burp.MyBurpExtension;
import burpgpt.http.GPTClient;

public class SettingsView extends JPanel implements PropertyChangeListener {

    private MyBurpExtension myBurpExtension;

    private JTextField apiKeyField;
    private JTextField modelIdField; // Changed from JComboBox to JTextField for custom input
    private JSpinner maxPromptSizeField;
    private JSpinner maxTokensField; // New field for maxTokens
    private JComboBox<String> apiProviderComboBox; // New field for API provider
    private JTextField localApiEndpointField; // New field for local API endpoint
    private JTextArea promptField;
    private JButton validateButton; // New button for validation

    private String model;

    public interface OnApplyButtonClickListener {
        void onApplyButtonClick();
    }

    private OnApplyButtonClickListener onApplyButtonClickListener;

    public void setOnApplyButtonClickListener(OnApplyButtonClickListener onApplyButtonClickListener) {
        this.onApplyButtonClickListener = onApplyButtonClickListener;
    }

    public SettingsView(MyBurpExtension myBurpExtension) {
        this.myBurpExtension = myBurpExtension;

        setLayout(new GridBagLayout());
        initComponents();

        myBurpExtension.addPropertyChangeListener(this);
    }

    private void initComponents() {
        createApiKeyField(0);
        createModelIdField(1); // Changed from createModelIdComboBox
        createApiProviderComboBox(2); // Add API provider field
        createLocalApiEndpointField(3); // Add local API endpoint field
        createMaxPromptSizeField(4);
        createMaxTokensField(5); // Add maxTokens field
        createPromptField(6);
        createPromptDescriptionLabel(7);
        createValidateButton(8); // Add validate button
        createApplyButton(9);
    }

    private void createApiKeyField(int y) {
        JLabel apiKeyLabel = new JLabel("API key:");
        apiKeyField = new JTextField(myBurpExtension.getApiKey(), 20);
        add(apiKeyLabel, createGridBagConstraints(0, y));
        add(apiKeyField, createGridBagConstraints(1, y));
    }

    private void createModelIdField(int y) {
        JLabel modelIdLabel = new JLabel("Model:");
        modelIdField = new JTextField(myBurpExtension.getModel(), 20); // Changed to JTextField for custom input
        model = myBurpExtension.getModel(); // Initialize model variable
        modelIdField.addActionListener(e -> model = modelIdField.getText().trim());
        add(modelIdLabel, createGridBagConstraints(0, y));
        add(modelIdField, createGridBagConstraints(1, y));
    }

    private void createApiProviderComboBox(int y) {
        JLabel apiProviderLabel = new JLabel("API Provider:");
        String[] providers = {"OpenAI", "Gemini", "Local", "ModelScope", "OpenRouter"};
        apiProviderComboBox = new JComboBox<>(providers);
        apiProviderComboBox.setSelectedItem(myBurpExtension.getApiProvider());
        add(apiProviderLabel, createGridBagConstraints(0, y));
        add(apiProviderComboBox, createGridBagConstraints(1, y));
    }

    private void createLocalApiEndpointField(int y) {
        JLabel localApiEndpointLabel = new JLabel("Local API Endpoint:");
        localApiEndpointField = new JTextField(myBurpExtension.getLocalApiEndpoint(), 20);
        add(localApiEndpointLabel, createGridBagConstraints(0, y));
        add(localApiEndpointField, createGridBagConstraints(1, y));
    }

    private void createMaxPromptSizeField(int y) {
        JLabel maxPromptSizeLabel = new JLabel("Maximum prompt size:");
        maxPromptSizeField = new JSpinner(
                new SpinnerNumberModel(myBurpExtension.getMaxPromptSize(), 1, Integer.MAX_VALUE, 1));
        add(maxPromptSizeLabel, createGridBagConstraints(0, y));
        add(maxPromptSizeField, createGridBagConstraints(1, y));
    }

    private void createMaxTokensField(int y) {
        JLabel maxTokensLabel = new JLabel("Maximum tokens:");
        maxTokensField = new JSpinner(
                new SpinnerNumberModel(myBurpExtension.getMaxTokens(), 1, Integer.MAX_VALUE, 1));
        add(maxTokensLabel, createGridBagConstraints(0, y));
        add(maxTokensField, createGridBagConstraints(1, y));
    }

    private void createPromptField(int y) {
        JLabel promptLabel = new JLabel("Prompt:");
        promptField = new JTextArea(myBurpExtension.getPrompt(), 14, 20);
        promptField.setWrapStyleWord(true);
        promptField.setLineWrap(true);
        JScrollPane promptScrollPane = new JScrollPane(promptField);
        add(promptLabel, createGridBagConstraints(0, y));
        add(promptScrollPane, createGridBagConstraints(1, y));
    }

    private void createPromptDescriptionLabel(int y) {
        JLabel promptDescriptionLabel = new JLabel(
                "<html>Refer to the repository (<a href=\"https://github.com/aress31/burpgpt\">https://github.com/aress31/burpgpt</a>) to learn how to optimally set the prompt for the GPT model.</html>");
        promptDescriptionLabel.putClientProperty("html.disable", null);
        add(promptDescriptionLabel, createGridBagConstraints(1, y));
    }

    private void createValidateButton(int y) {
        validateButton = new JButton("Validate Connection");
        validateButton.addActionListener(e -> validateConnection());
        validateButton.setBackground(UIManager.getColor("Burp.burpOrange"));
        validateButton.setForeground(Color.WHITE);
        validateButton.setFont(validateButton.getFont().deriveFont(Font.BOLD));
        add(validateButton, createGridBagConstraints(1, y));
    }

    private void createApplyButton(int y) {
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> applySettings());
        applyButton.setBackground(UIManager.getColor("Burp.burpOrange"));
        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(applyButton.getFont().deriveFont(Font.BOLD));
        add(applyButton, createGridBagConstraints(1, y));
    }

    private void validateConnection() {
        String apiKey = apiKeyField.getText().trim();
        String modelId = modelIdField.getText().trim();
        String apiProvider = (String) apiProviderComboBox.getSelectedItem();
        String localApiEndpoint = localApiEndpointField.getText().trim();
        int maxTokens = (int) maxTokensField.getValue();

        if (apiKey.isEmpty() || modelId.isEmpty()) {
            JOptionPane.showMessageDialog(SettingsView.this,
                    "API key and model are required for validation", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create a temporary GPTClient for validation
            GPTClient validationClient = new GPTClient(apiKey, modelId, myBurpExtension.getPrompt(), myBurpExtension.getLogging());
            validationClient.updateSettings(apiKey, modelId, myBurpExtension.getMaxPromptSize(), maxTokens, apiProvider, localApiEndpoint, myBurpExtension.getPrompt());

            // Perform a simple validation request (you might want to implement a specific validation method in GPTClient)
            boolean isValid = validationClient.validateConnection();

            if (isValid) {
                JOptionPane.showMessageDialog(SettingsView.this,
                        "Connection successful! The API key and model are valid.", "Validation Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(SettingsView.this,
                        "Connection failed. Please check your API key, model, and network settings.", "Validation Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SettingsView.this,
                    "Connection failed: " + e.getMessage(), "Validation Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applySettings() {
        String newApiKey = apiKeyField.getText().trim();
        String newModelId = modelIdField.getText().trim(); // Changed from JComboBox to JTextField
        String newApiProvider = (String) apiProviderComboBox.getSelectedItem(); // Get API provider
        String newLocalApiEndpoint = localApiEndpointField.getText().trim(); // Get local API endpoint
        int newMaxPromptSize = (int) maxPromptSizeField.getValue();
        int newMaxTokens = (int) maxTokensField.getValue(); // Get maxTokens value
        String newPromptText = promptField.getText().trim();

        if (newApiKey.isEmpty() || newModelId.isEmpty() || newPromptText.isEmpty() || newMaxPromptSize <= 0 || newMaxTokens <= 0) {
            JOptionPane.showMessageDialog(SettingsView.this,
                    "All fields are required and max prompt size and max tokens must be greater than 0", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        myBurpExtension.updateSettings(newApiKey, newModelId, newMaxPromptSize, newMaxTokens, newApiProvider, newLocalApiEndpoint, newPromptText);

        if (onApplyButtonClickListener != null) {
            onApplyButtonClickListener.onApplyButtonClick();
        }
    }

    private GridBagConstraints createGridBagConstraints(int x, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.weightx = x == 0 ? 0 : 1;
        constraints.weighty = 0.5;
        constraints.insets = new Insets(16, x == 0 ? 16 : 4, 16, x == 0 ? 4 : 16);
        constraints.anchor = y != 5 ? GridBagConstraints.LINE_START : GridBagConstraints.LINE_END;
        constraints.fill = (x == 0 || y == 5) ? GridBagConstraints.NONE : GridBagConstraints.HORIZONTAL;
        return constraints;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("settingsChanged".equals(evt.getPropertyName())) {
            String[] newValues = (String[]) evt.getNewValue();
            apiKeyField.setText(newValues[0]);
            modelIdField.setText(newValues[1]); // Changed from JComboBox to JTextField
            apiProviderComboBox.setSelectedItem(newValues[2]); // Update API provider field
            localApiEndpointField.setText(newValues[3]); // Update local API endpoint field
            maxPromptSizeField.setValue(Integer.parseInt(newValues[4]));
            maxTokensField.setValue(Integer.parseInt(newValues[5])); // Update maxTokens field
            promptField.setText(newValues[6]);
        }
    }
}
