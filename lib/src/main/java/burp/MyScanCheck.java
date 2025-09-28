package burp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.scanner.AuditResult;
import burp.api.montoya.scanner.ConsolidationAction;
import burp.api.montoya.scanner.ScanCheck;
import burp.api.montoya.scanner.audit.insertionpoint.AuditInsertionPoint;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.scanner.audit.issues.AuditIssueConfidence;
import burp.api.montoya.scanner.audit.issues.AuditIssueSeverity;
import burpgpt.gpt.GPTRequest;
import burpgpt.gpt.GPTResponse;
import burpgpt.http.GPTClient;
import lombok.Setter;

public class MyScanCheck implements ScanCheck {

    private Logging logging;

    @Setter
    private GPTClient gptClient;

    public MyScanCheck(GPTClient gptClient, Logging logging) {
        this.gptClient = gptClient;
        this.logging = logging;
    }

    @Override
    public AuditResult activeAudit(HttpRequestResponse httpRequestResponse, AuditInsertionPoint auditInsertionPoint) {
        return AuditResult.auditResult(new ArrayList<>());
    }

    @Override
    public AuditResult passiveAudit(HttpRequestResponse httpRequestResponse) {
        try {
            Pair<GPTRequest, GPTResponse> gptResults = gptClient.identifyVulnerabilities(httpRequestResponse);
            List<AuditIssue> auditIssues = createAuditIssuesFromGPTResponse(gptResults, httpRequestResponse);
            return AuditResult.auditResult(auditIssues);
        } catch (IOException e) {
            logging.raiseErrorEvent(e.getMessage());
            return AuditResult.auditResult(new ArrayList<>());
        }
    }

    @Override
    public ConsolidationAction consolidateIssues(AuditIssue newIssue, AuditIssue existingIssue) {
        return newIssue.equals(existingIssue) ? ConsolidationAction.KEEP_EXISTING
                : ConsolidationAction.KEEP_BOTH;
    }

    private List<AuditIssue> createAuditIssuesFromGPTResponse(Pair<GPTRequest, GPTResponse> gptResults,
            HttpRequestResponse httpRequestResponse) {
        List<AuditIssue> auditIssues = new ArrayList<>();
        GPTRequest gptRequest = gptResults.getLeft();
        GPTResponse gptResponse = gptResults.getRight();

        // Extract vulnerabilities from GPT response
        List<GPTResponse.Vulnerability> vulnerabilities = gptResponse.extractVulnerabilities();

        if (!vulnerabilities.isEmpty()) {
            // Create audit issues for each vulnerability
            for (GPTResponse.Vulnerability vulnerability : vulnerabilities) {
                String escapedPrompt = StringEscapeUtils.escapeHtml4(gptRequest.getPrompt().trim()).replace("\n", "<br />");
                String issueBackground = String.format(
                        "The AI model identified a potential security vulnerability:" + "<br>"
                                + "<ul>"
                                + "<li>Model: %s</li>"
                                + "<li>Maximum prompt size: %s</li>"
                                + "<li>Prompt:<br><br>%s</li>"
                                + "</ul>",
                        gptRequest.getModel(), gptRequest.getMaxPromptSize(), escapedPrompt);

                String detail = String.format(
                        "<b>%s</b><br><br>%s<br><br><b>Recommendation:</b><br>%s",
                        vulnerability.getName(),
                        vulnerability.getDescription(),
                        vulnerability.getRecommendation());

                AuditIssueSeverity severity = getAuditIssueSeverity(vulnerability.getSeverity());

                AuditIssue auditIssue = AuditIssue.auditIssue(
                        vulnerability.getName(),
                        detail,
                        null,
                        httpRequestResponse.request().url(),
                        severity,
                        AuditIssueConfidence.TENTATIVE,
                        issueBackground,
                        null,
                        null,
                        httpRequestResponse);
                auditIssues.add(auditIssue);
            }
        } else if (gptResponse.getChoices() != null && !gptResponse.getChoices().isEmpty()) {
            // Fallback to original behavior if no vulnerabilities were extracted
            String escapedPrompt = StringEscapeUtils.escapeHtml4(gptRequest.getPrompt().trim()).replace("\n", "<br />");
            String issueBackground = String.format(
                    "The AI model generated insights using the following parameters:" + "<br>"
                            + "<ul>"
                            + "<li>Model: %s</li>"
                            + "<li>Maximum prompt size: %s</li>"
                            + "<li>Prompt:<br><br>%s</li>"
                            + "</ul>",
                    gptRequest.getModel(), gptRequest.getMaxPromptSize(), escapedPrompt);

            String choiceText = gptResponse.getChoices().get(0).getText();
            String escapedDetail = StringEscapeUtils.escapeHtml4(choiceText.trim()).replace("\n", "<br />");

            AuditIssue auditIssue = AuditIssue.auditIssue(
                    "AI-generated insights",
                    escapedDetail,
                    null,
                    httpRequestResponse.request().url(),
                    AuditIssueSeverity.INFORMATION,
                    AuditIssueConfidence.TENTATIVE,
                    issueBackground,
                    null,
                    null,
                    httpRequestResponse);
            auditIssues.add(auditIssue);
        }

        return auditIssues;
    }

    private AuditIssueSeverity getAuditIssueSeverity(String severity) {
        switch (severity.toLowerCase()) {
            case "high":
                return AuditIssueSeverity.HIGH;
            case "medium":
                return AuditIssueSeverity.MEDIUM;
            case "low":
                return AuditIssueSeverity.LOW;
            default:
                return AuditIssueSeverity.INFORMATION;
        }
    }
}
