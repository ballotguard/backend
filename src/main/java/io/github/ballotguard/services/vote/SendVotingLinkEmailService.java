package io.github.ballotguard.services.vote;

import io.github.ballotguard.entities.election.Voter;
import io.github.ballotguard.utilities.VotingStringUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity<?> sendVotingLinkToAllVoters(ArrayList<Voter> voters, long startTimeEpochMillis, long endTimeEpochMillis, String electionName, String electionDescription, String electionId) throws Exception {
        for (Voter voter : voters) {
            String votingLink = "https://ballotguard.vercel.app/"+votingStringUtil.encrypt(electionId, voter.getVoterId());
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
          <style>
            body {
              margin: 0;
              padding: 0;
              background-color: #f5f5f5;
              font-family: 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
            }
            .container {
              max-width: 600px;
              margin: 40px auto;
              background-color: #ffffff;
              border-radius: 12px;
              overflow: hidden;
              box-shadow: 0 2px 10px rgba(0,0,0,0.05);
              border: 1px solid #e0e0e0;
            }
            .top-bar {
              background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
       
              height: 8px;
            }
            .content {
              padding: 32px 40px;
              text-align: left;
            }
            .content h2 {
              margin-top: 0;
              font-size: 22px;
              color: #2d3748;
              font-weight: 600;
            }
            .content h3 {
              font-size: 18px;
              color: #4a5568;
              margin-bottom: 10px;
            }
            .content p {
              font-size: 15px;
              color: #4a5568;
              line-height: 1.6;
            }
            .button-container {
              text-align: center;
              margin: 30px 0;
            }
            .btn-vote {
              background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
              color: #ffffff;
              padding: 14px 28px;
              font-size: 16px;
              text-decoration: none;
              font-weight: 600;
              border-radius: 6px;
              display: inline-block;
              transition: transform 0.2s, box-shadow 0.2s;
              box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            .btn-vote:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
            }
            .footer {
              font-size: 12px;
              color: #718096;
              text-align: center;
              padding: 20px;
              border-top: 1px solid #edf2f7;
            }
            .footer a {
              color: #667eea;
              text-decoration: none;
            }
            .note {
              font-size: 13px;
              color: #718096;
              text-align: center;
              margin-top: 20px;
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
              <p>This link will take you to the voting page. You can cast your vote by clicking the button below.</p>
              <p><strong>Note:</strong> You can only vote once. Please make sure to cast your vote carefully.</p>
              
              <div class="button-container">
                <a href="%s" class="btn-vote" target="_blank">Vote Now</a>
              </div>
              
              <p class="note">Voting starts at: <strong>%s</strong><br/>
              Voting ends at: <strong>%s</strong></p>
              
              <p class="note">If you did not expect this email, you can safely ignore it.</p>
            </div>
            <div class="footer">
              &copy; Ballotguard | <a href="https://github.com/ballotguard">github.com/ballotguard</a>
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
