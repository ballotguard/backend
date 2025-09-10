package io.github.ballotguard.services.vote;

import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.utilities.VotingStringUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
@Slf4j
public class SendVotingLinkEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private VotingStringUtil votingStringUtil;

    @Value("${app.cors.allowed-origin}")
    private String corsAllowedOrigin;

    public ResponseEntity<?> sendVotingLinkToAllVoters(ArrayList<Voter> voters, long startTimeEpochMillis, long endTimeEpochMillis, String electionName, String electionDescription, String electionId) throws Exception {
        for (Voter voter : voters) {
            String votingLink = corsAllowedOrigin+"/election/"+electionId+"/"+voter.getVoterId();
            System.out.println(votingLink);
            sendVotingEmail(voter.getVoterEmail(), "Your private voting link", votingLink, startTimeEpochMillis, endTimeEpochMillis, electionName, electionDescription);
        }
        return ResponseEntity.ok().build();
    }

    private String formatEpochMillis(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a z"));
    }

    private String votingEmailBodyHtml(String electionName, String electionDescription, String votingLink, long startEpochMillis, long endEpochMillis) {
        String startTime = formatEpochMillis(startEpochMillis);
        String endTime = formatEpochMillis(endEpochMillis);

        return String.format("""
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
          <style>
            /* Base */
            body {
              margin: 0;
              padding: 0;
              background-color: #020617; /* site dark bg */
              font-family: 'Poppins', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
              color: #e5e7eb; /* slate-200 */
            }

            /* Container */
            .container {
              max-width: 600px;
              margin: 40px auto;
              background-color: #0b1220; /* deep panel */
              border-radius: 14px;
              overflow: hidden;
              box-shadow: 0 10px 30px rgba(0,0,0,0.35);
              border: 1px solid rgba(255,255,255,0.08);
            }

            /* Accent bar (no purple) */
            .top-bar {
              height: 8px;
              background: linear-gradient(90deg, #93c5fd 0%%, #22d3ee 50%%, #10b981 100%%); /* sky -> cyan -> emerald */
            }

            /* Content */
            .content {
              padding: 32px 40px;
              text-align: left;
            }
            .content h2 {
              margin: 0 0 6px 0;
              font-size: 24px;
              color: #ffffff;
              font-weight: 700;
              letter-spacing: 0.2px;
            }
            .content h3 {
              margin: 0 0 14px 0;
              font-size: 18px;
              color: #cbd5e1; /* slate-300 */
              font-weight: 600;
            }
            .content p {
              font-size: 15px;
              color: #cbd5e1; /* slate-300 */
              line-height: 1.7;
              margin: 0 0 12px 0;
            }

            /* Meta rows */
            .meta {
              margin: 18px 0 8px 0;
              padding: 12px 14px;
              background-color: rgba(255,255,255,0.03);
              border: 1px solid rgba(255,255,255,0.08);
              border-radius: 10px;
            }
            .meta strong {
              color: #e2e8f0;
              font-weight: 600;
            }

            /* CTA */
            .button-container {
              text-align: center;
              margin: 30px 0 18px 0;
            }
            .btn-vote {
              background: #000000; /* matches site buttons: black bg, white text */
              color: #ffffff;
              padding: 14px 28px;
              font-size: 16px;
              text-decoration: none;
              font-weight: 700;
              border-radius: 10px;
              display: inline-block;
              border: 1px solid rgba(255,255,255,0.12);
              transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
              box-shadow: 0 6px 16px rgba(0,0,0,0.35);
            }
            .btn-vote:hover {
              transform: translateY(-2px);
              box-shadow: 0 10px 24px rgba(0,0,0,0.45);
              background-color: #0a0a0a;
            }

            /* Notes and footer */
            .note {
              font-size: 13px;
              color: #94a3b8; /* slate-400 */
              text-align: center;
              margin-top: 14px;
            }
            .footer {
              font-size: 12px;
              color: #94a3b8;
              text-align: center;
              padding: 18px 16px 22px 16px;
              border-top: 1px solid rgba(255,255,255,0.08);
              background-color: rgba(255,255,255,0.02);
            }
            .footer a {
              color: #cbd5e1;
              text-decoration: none;
            }

            /* Small screens */
            @media (max-width: 640px) {
              .content { padding: 24px 20px; }
              .content h2 { font-size: 22px; }
              .content h3 { font-size: 16px; }
            }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="top-bar"></div>
            <div class="content">
              <h2>Your Vote is Requested</h2>
              <h3>%s</h3>
              <p>%s</p>
              <p>This secure link will take you to the voting page. You can cast your vote by clicking the button below.</p>
              <p><strong>Note:</strong> You can only vote once. Please make sure to cast your vote carefully.</p>

              <div class="button-container">
                <a href="%s" class="btn-vote" target="_blank" rel="noopener noreferrer">Vote Now</a>
              </div>

              <div class="meta">
                <p>Voting starts at: <strong>%s</strong></p>
                <p>Voting ends at: <strong>%s</strong></p>
              </div>

              <p class="note">If you did not expect this email, you can safely ignore it.</p>
            </div>
            <div class="footer">
              &copy; Ballotguard &nbsp;|&nbsp; <a href="https://github.com/ballotguard">github.com/ballotguard</a>
            </div>
          </div>
        </body>
        </html>
        """, electionName, electionDescription, votingLink, startTime, endTime);
    }

    @Transactional
    public ResponseEntity<?> sendVotingEmail(String to, String subject, String votingLink, long startTimeEpochMillis, long endTimeEpochMillis, String electionName, String electionDescription) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(votingEmailBodyHtml(electionName, electionDescription, votingLink, startTimeEpochMillis, endTimeEpochMillis), true);

            javaMailSender.send(message);
            log.debug("Voting link email sent to " + to);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to send voting email", e);
            throw new MessagingException("Failed to send voting email", e);
        }
    }
}
