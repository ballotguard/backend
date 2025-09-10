package io.github.ballotguard.services.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class VerificationEmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    private String verificationEmailBodyHtml(String verificationCode, String verificationMessage) {
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
                              margin: 0 0 8px 0;
                              font-size: 24px;
                              color: #ffffff;
                              font-weight: 700;
                              letter-spacing: 0.2px;
                            }
                            .content p {
                              font-size: 15px;
                              color: #cbd5e1; /* slate-300 */
                              line-height: 1.7;
                              margin: 0 0 12px 0;
                            }
                            /* Code box */
                            .code-container {
                              background-color: rgba(255,255,255,0.03);
                              border: 1px solid rgba(255,255,255,0.08);
                              border-radius: 12px;
                              padding: 18px;
                              margin: 22px 0;
                            }
                            .verification-code {
                              font-family: 'Courier New', ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
                              font-size: 28px;
                              font-weight: 700;
                              color: #ffffff;
                              text-align: center;
                              letter-spacing: 6px;
                            }
                            /* Footer */
                            .footer {
                              font-size: 12px;
                              color: #94a3b8; /* slate-400 */
                              text-align: center;
                              padding: 18px 16px 22px 16px;
                              border-top: 1px solid rgba(255,255,255,0.08);
                              background-color: rgba(255,255,255,0.02);
                            }
                            .footer a {
                              color: #cbd5e1;
                              text-decoration: none;
                            }
                            .expiry-note {
                              font-size: 13px;
                              color: #94a3b8; /* slate-400 */
                              text-align: center;
                              margin-top: 16px;
                            }
                            @media (max-width: 640px) {
                              .content { padding: 24px 20px; }
                              .content h2 { font-size: 22px; }
                              .verification-code { font-size: 24px; letter-spacing: 4px; }
                            }
                          </style>
                        </head>
                        <body>
                          <div class="container">
                            <div class="top-bar"></div>
                            <div class="content">
                              <h2>Verify your email address</h2>
                              <p>%s</p>
                              <div class="code-container">
                                <div class="verification-code">%s</div>
                              </div>
                              <p class="expiry-note">This code expires in 5 minutes. If you didn’t request this, you can safely ignore this email.</p>
                            </div>
                            <div class="footer">
                              &copy; Ballotguard &nbsp;•&nbsp; <a href="https://github.com/ballotguard">github.com/ballotguard</a>
                            </div>
                          </div>
                        </body>
                        </html>
                        """,
                verificationMessage,
                verificationCode
        );
    }


    @Transactional
    public ResponseEntity<?> sendEmail(String to, String subject, String verificationCode, String verificationMessage) throws MessagingException {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(verificationEmailBodyHtml(verificationCode, verificationMessage), true); // 'true' enables HTML

            javaMailSender.send(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new MessagingException("Failed to send email", e);
        }
    }
}
