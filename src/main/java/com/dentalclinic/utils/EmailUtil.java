package com.dentalclinic.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtil {

    // Update these with your email settings
    private static final String FROM_EMAIL = "sunrisedentalclinicsystem@gmail.com";
    private static final String FROM_PASSWORD = "ejezgynbnwmuecud"; // Use App Password for Gmail
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    public static boolean sendPasswordRecoveryEmail(String toEmail, String username, String newPassword) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Sunrise Dental Clinic"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Sunrise Dental Clinic - Password Recovery");

            String htmlContent = buildRecoveryEmailHTML(username, newPassword);
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("Recovery email sent to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println("Failed to send recovery email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String buildRecoveryEmailHTML(String username, String newPassword) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }");
        html.append(".header { background: #1a2530; padding: 20px; border-radius: 10px 10px 0 0; text-align: center; margin: -30px -30px 20px -30px; }");
        html.append(".header h1 { color: #3CA6A6; margin: 0; font-size: 24px; }");
        html.append(".header p { color: white; margin: 5px 0 0; }");
        html.append(".content { padding: 10px 0; }");
        html.append(".credential-box { background: #f8f9fa; border-left: 4px solid #3CA6A6; padding: 15px 20px; margin: 15px 0; border-radius: 5px; }");
        html.append(".credential-box .label { font-weight: 600; color: #2c3e50; }");
        html.append(".credential-box .value { color: #3CA6A6; font-weight: 700; font-size: 16px; }");
        html.append(".footer { text-align: center; padding-top: 20px; border-top: 1px solid #eaeaea; color: #7f8c8d; font-size: 12px; }");
        html.append(".btn { display: inline-block; background: #3CA6A6; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 10px 0; }");
        html.append(".btn:hover { background: #2D8C8C; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>Sunrise Dental Clinic</h1>");
        html.append("<p>Password Recovery</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");
        html.append("<p>Hello <strong>" + username + "</strong>,</p>");
        html.append("<p>We received a request to reset your password for the Sunrise Dental Clinic Management System.</p>");
        html.append("<p>Your account has been recovered with the following credentials:</p>");
        html.append("<div class=\"credential-box\">");
        html.append("<div><span class=\"label\">Username:</span> <span class=\"value\">" + username + "</span></div>");
        html.append("<div style=\"margin-top: 8px;\"><span class=\"label\">New Password:</span> <span class=\"value\">" + newPassword + "</span></div>");
        html.append("</div>");
        html.append("<p style=\"color: #e74c3c; font-size: 14px;\"><strong>Important:</strong> Please change your password after logging in for security reasons.</p>");
        html.append("<div style=\"text-align: center;\">");
        html.append("<a href=\"http://localhost:8080/dental_clinic_webapp_war_exploded/login\" class=\"btn\">Login to Your Account</a>");
        html.append("</div>");
        html.append("<p style=\"margin-top: 15px; font-size: 13px; color: #7f8c8d;\">If you did not request this password reset, please ignore this email or contact system administrator immediately.</p>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p>2026 Sunrise Dental Clinic Management System</p>");
        html.append("<p>This is an automated message, please do not reply to this email.</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
}