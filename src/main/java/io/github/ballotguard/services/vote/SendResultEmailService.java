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
        <html lang="en">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
          <style>
            body {
              margin: 0;
              padding: 20px;
              background-color: #020617; /* site dark bg */
              font-family: 'Poppins', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
              color: #e5e7eb; /* slate-200 */
            }
            .container {
              max-width: 600px;
              margin: 0 auto;
              background-color: #0b1220; /* deep panel */
              border-radius: 14px;
              overflow: hidden;
              box-shadow: 0 10px 30px rgba(0,0,0,0.35);
              border: 1px solid rgba(255,255,255,0.08);
            }
            .top-bar {
              height: 8px;
              background: linear-gradient(90deg, #93c5fd 0%%, #22d3ee 50%%, #10b981 100%%); /* sky -> cyan -> emerald */
            }
            .content {
              padding: 32px 40px;
              text-align: left;
            }
            h2 {
              margin: 0 0 12px 0;
              font-size: 24px;
              color: #ffffff;
              font-weight: 700;
              letter-spacing: 0.2px;
            }
            p, li {
              font-size: 15px;
              color: #cbd5e1; /* slate-300 */
              line-height: 1.7;
              margin: 0 0 12px 0;
            }
            .meta-grid { margin: 16px 0 8px 0; }
            .meta-row {
              display: flex;
              justify-content: space-between;
              gap: 16px;
              padding: 10px 12px;
              border: 1px solid rgba(255,255,255,0.08);
              border-radius: 10px;
              background-color: rgba(255,255,255,0.03);
              margin-bottom: 10px;
            }
            .meta-row strong { color: #e2e8f0; font-weight: 600; }
            .stats { display: flex; gap: 12px; margin: 18px 0 10px 0; flex-wrap: wrap; }
            .pill {
              display: inline-flex; align-items: center; gap: 8px;
              padding: 10px 12px; border-radius: 999px;
              background-color: rgba(255,255,255,0.05);
              border: 1px solid rgba(255,255,255,0.08);
            }
            .divider {
              height: 1px; width: 100%%;
              background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
              margin: 18px 0;
            }
            .section-title {
              font-size: 16px; font-weight: 600; color: #e2e8f0;
              margin: 14px 0 8px 0;
            }
            .content-box {
              padding: 14px 16px;
              border: 1px solid rgba(255,255,255,0.08);
              border-radius: 10px;
              background-color: rgba(255,255,255,0.02);
            }
            .footer {
              font-size: 12px; color: #94a3b8; text-align: center;
              padding: 18px 16px 22px 16px;
              border-top: 1px solid rgba(255,255,255,0.08);
              background-color: rgba(255,255,255,0.02);
            }
            .footer a { color: #cbd5e1; text-decoration: none; }
            @media (max-width: 640px) {
              .content { padding: 24px 20px; }
              h2 { font-size: 22px; }
            }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="top-bar"></div>
            <div class="content">
              <h2>Election Summary</h2>

              <div class="meta-grid">
                <div class="meta-row"><strong>Name</strong><span>%s</span></div>
                <div class="meta-row"><strong>Description</strong><span>%s</span></div>
              </div>

              <div class="stats">
                <div class="pill"><strong>Total Voters</strong><span>%d</span></div>
                <div class="pill"><strong>Total Votes</strong><span>%d</span></div>
              </div>

              <div class="divider"></div>

              <div class="section">
                <div class="section-title">Results</div>
                <div class="content-box">
                  %s
                </div>
              </div>

              <div class="section">
                <div class="section-title">Participants</div>
                <div class="content-box">
                  %s
                </div>
              </div>
            </div>

            <div class="footer">
              &copy; Ballotguard &nbsp;|&nbsp; <a href="https://github.com/ballotguard">github.com/ballotguard</a>
            </div>
          </div>
        </body>
        </html>
        """,
                electionName,
                electionDescription,
                totalVoters,
                totalVotes,
                optionsHtml,
                voterListHtml);
    }
}
