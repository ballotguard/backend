package io.github.ballotguard.services;

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
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    private String emailBodyHtml(String verificationCode, String verificationMessage) {
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
                .content p {
                  font-size: 15px;
                  color: #4a5568;
                  line-height: 1.6;
                }
                .code-container {
                  background-color: #f8fafc;
                  border: 1px solid #e2e8f0;
                  border-radius: 6px;
                  padding: 16px;
                  margin: 24px 0;
                  position: relative;
                }
                .verification-code {
                  font-family: 'Courier New', monospace;
                  font-size: 24px;
                  font-weight: bold;
                  color: #2d3748;
                  text-align: center;
                  letter-spacing: 2px;
                }
                .copy-btn {
                  position: absolute;
                  right: 12px;
                  top: 12px;
                  background-color: #edf2f7;
                  border: none;
                  border-radius: 4px;
                  padding: 4px 8px;
                  font-size: 12px;
                  color: #4a5568;
                  cursor: pointer;
                  transition: all 0.2s;
                }
                .copy-btn:hover {
                  background-color: #e2e8f0;
                }
                .button-container {
                  text-align: center;
                  margin: 30px 0;
                }
                .btn-verify {
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
                .btn-verify:hover {
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
                .expiry-note {
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
                  <h2>Verify your email address</h2>
                  <p>%s</p>
                  
                  <div class="code-container">
                    
                    <div class="verification-code">%s</div>
                  </div>
                  
           
                  
                  <p class="expiry-note">This code expires in 5 minutes. If you didn't request this, you can safely ignore this email.</p>
                </div>
                <div class="footer">
                  &copy;Ballotguard • <a href="https://github.com/ballotguard">github.com/ballotguard</a>
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
            helper.setText(emailBodyHtml(verificationCode, verificationMessage), true); // 'true' enables HTML

            javaMailSender.send(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new MessagingException("Failed to send email", e);
        }
    }
}
