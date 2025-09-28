package burpgpt.gpt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GPTResponse {
    private List<Choice> choices;
    private String model;
    private String id;
    @SerializedName("created")
    private long createdTimestamp;
    @SerializedName("usage")
    private Usage usage;

    public GPTResponse() {
        this.choices = new ArrayList<>();
    }

    public GPTResponse(List<Choice> choices) {
        this.choices = choices;
    }

    @Getter
    @Setter
    public class Choice {
        private String text;
        private int index;
        private Object logprobs; // or use a specific class structure if needed
        @SerializedName("finish_reason")
        private String finishReason;

        @Override
        public String toString() {
            return "Choice{" +
                    "text='" + text + '\'' +
                    ", index=" + index +
                    ", logprobs=" + logprobs +
                    ", finishReason='" + finishReason + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class Vulnerability {
        private String name;
        private String description;
        private String severity;
        private String recommendation;

        public Vulnerability(String name, String description, String severity, String recommendation) {
            this.name = name;
            this.description = description;
            this.severity = severity;
            this.recommendation = recommendation;
        }

        @Override
        public String toString() {
            return "Vulnerability{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", severity='" + severity + '\'' +
                    ", recommendation='" + recommendation + '\'' +
                    '}';
        }
    }

    public List<String> getChoiceTexts() {
        List<String> choiceTexts = new ArrayList<>();
        for (Choice choice : choices) {
            choiceTexts.add(choice.getText());
        }
        return choiceTexts;
    }

    public List<Vulnerability> extractVulnerabilities() {
        List<Vulnerability> vulnerabilities = new ArrayList<>();

        if (choices != null && !choices.isEmpty()) {
            String responseText = choices.get(0).getText();

            // Pattern to match vulnerabilities in the format:
            // - Vulnerability Name: Brief description of vulnerability
            Pattern pattern = Pattern.compile("-\\s*([^:]+):\\s*(.+)");
            Matcher matcher = pattern.matcher(responseText);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                String description = matcher.group(2).trim();

                // Determine severity based on vulnerability name
                String severity = determineSeverity(name);

                // Create vulnerability object
                Vulnerability vulnerability = new Vulnerability(
                    name,
                    description,
                    severity,
                    "Review the application code and implement appropriate security measures."
                );

                vulnerabilities.add(vulnerability);
            }
        }

        return vulnerabilities;
    }

    private String determineSeverity(String vulnerabilityName) {
        // Simple severity determination based on keywords
        String lowerName = vulnerabilityName.toLowerCase();

        if (lowerName.contains("sql") || lowerName.contains("injection") || lowerName.contains("xss")) {
            return "High";
        } else if (lowerName.contains("csrf") || lowerName.contains("authentication") || lowerName.contains("authorization")) {
            return "Medium";
        } else {
            return "Low";
        }
    }

    @Getter
    public static class Usage {
        private long promptTokens;
        private long completionTokens;
        private long totalTokens;

        @Override
        public String toString() {
            return "Usage{" +
                    "promptTokens=" + promptTokens +
                    ", completionTokens=" + completionTokens +
                    ", totalTokens=" + totalTokens +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GPTResponse{" +
                "choices=" + choices +
                ", model='" + model + '\'' +
                ", id='" + id + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", usage=" + usage +
                '}';
    }
}
