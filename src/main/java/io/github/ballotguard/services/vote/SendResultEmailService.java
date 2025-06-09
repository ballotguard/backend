package io.github.ballotguard.services.vote;

import io.github.ballotguard.entities.election.Voter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SendResultEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Sends the same election summary to all voters and the election owner
     * in a single email (using BCC for privacy).
     */
    public void sendResultSummaryToAllVotersAndElectionCreator(
            List<Voter> voters,
            String ownerEmail,
            Map<String, Object> resultMap
    ) throws MessagingException {
        String subject = "Election Summary: " + resultMap.get("electionName");
        String body    = buildEmailHtml(resultMap);

        List<String> allEmails = voters.stream()
                .map(Voter::getVoterEmail)
                .collect(Collectors.toList());
        allEmails.add(ownerEmail);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setBcc(allEmails.toArray(new String[0]));

        javaMailSender.send(message);
        log.debug("Election summary email sent to {} recipients", allEmails.size());
        ResponseEntity.ok().build();
    }

    /**
     * Builds the HTML body for the election summary email.
     */
    private String buildEmailHtml(Map<String, Object> data) {
        String electionName        = data.get("electionName").toString();
        String electionDescription = data.get("electionDescription").toString();
        int totalVotes             = Integer.parseInt(data.get("totalVotes").toString());
        int totalVoters            = Integer.parseInt(data.get("totalVoters").toString());

        // Option vote counts (shown at the top)
        StringBuilder optionsHtml = new StringBuilder();
        optionsHtml.append("<h3>Vote Count Per Option</h3><ul>");
        List<Map<String, Object>> options = (List<Map<String, Object>>) data.get("options");
        for (Map<String, Object> option : options) {
            optionsHtml.append("<li><strong>")
                    .append(option.get("optionName"))
                    .append(":</strong> ")
                    .append(option.get("votes"))
                    .append(" votes</li>");
        }
        optionsHtml.append("</ul>");

        // Voter emails list (non-collapsible, shown below)
        StringBuilder voterListHtml = new StringBuilder();
        voterListHtml.append("<h3>List of Voter Emails</h3><ul style=\"margin-top: 10px; padding-left: 20px;\">");
        List<Map<String, Object>> voters = (List<Map<String, Object>>) data.get("voters");
        for (Map<String, Object> voter : voters) {
            voterListHtml.append("<li>")
                    .append(voter.get("voterEmail"))
                    .append("</li>");
        }
        voterListHtml.append("</ul>");

        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; border: 1px solid #e0e0e0;">
                        <h2 style="color: #2d3748;">Election Summary</h2>
                        <p><strong>Name:</strong> %s</p>
                        <p><strong>Description:</strong> %s</p>
                        <p><strong>Total Voters:</strong> %d</p>
                        <p><strong>Total Votes Cast:</strong> %d</p>
                        %s
                        <br/>
                        %s
                        <p style="font-size: 12px; color: #666; margin-top: 30px; text-align: center;">
                            &copy; Ballotguard — <a href="https://github.com/ballotguard" style="color: #667eea;">GitHub</a>
                        </p>
                    </div>
                </body>
                </html>
                """,
                electionName,
                electionDescription,
                totalVoters,
                totalVotes,
                optionsHtml,
                voterListHtml
        );
    }
}
