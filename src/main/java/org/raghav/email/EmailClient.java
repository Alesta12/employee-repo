package org.raghav.email;

import org.raghav.employeeService.beans.Employee;
import org.raghav.otpService.OtpClient;
import org.raghav.redis.ObjectRedisClient;
import org.raghav.redis.StringRedisClient;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailClient {

    private final OtpClient otpClient;
    private final ObjectRedisClient<Employee> employeeObjectRedisClient;
    private final StringRedisClient stringRedisClient;

    private final String fromEmail;
    private final String smtpUser;
    private final String smtpPass;

    public EmailClient(OtpClient otpClient,
                       ObjectRedisClient<Employee> employeeObjectRedisClient,
                       StringRedisClient stringRedisClient) {
        this.otpClient = otpClient;
        this.employeeObjectRedisClient = employeeObjectRedisClient;
        this.stringRedisClient = stringRedisClient;

        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()
                .load();

        // Mailjet credentials from .env
        this.fromEmail = dotenv.get("MAILJET_EMAIL");   // verified email in Mailjet
        this.smtpUser  = dotenv.get("MAILJET_API_KEY"); // Mailjet API Key
        this.smtpPass  = dotenv.get("MAILJET_SECRET");  // Mailjet Secret Key

        if (fromEmail == null || fromEmail.isEmpty() ||
                smtpUser == null || smtpUser.isEmpty() ||
                smtpPass == null || smtpPass.isEmpty()) {
            throw new RuntimeException("Mailjet SMTP configuration missing in .env");
        }
    }

    public void sendOtp(Employee employee) {
        String toEmail = employee.getEmail();
        if (toEmail == null || toEmail.isEmpty()) {
            throw new RuntimeException("Recipient email is null or empty");
        }

        String otp = otpClient.getOtp();
        if (otp == null || otp.isEmpty()) {
            throw new RuntimeException("OTP is null or empty");
        }

       // System.out.println("Sending OTP from: " + fromEmail + " to: " + toEmail + " OTP: " + otp);

        // Setup Mailjet SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "in-v3.mailjet.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "in-v3.mailjet.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPass);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject("Your OTP Code");
            msg.setText("Your OTP is: " + otp);
            Transport.send(msg);
            Thread.startVirtualThread(() -> {
                try {
                    employeeObjectRedisClient.set(toEmail, employee, 300); // 5 minutes
                    stringRedisClient.set("otp_" + toEmail, otp, 300);     // 5 minutes
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP: " + e.getMessage(), e);
        }
    }
}
