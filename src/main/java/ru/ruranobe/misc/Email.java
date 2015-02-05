package ru.ruranobe.misc;

import java.util.Properties;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email 
{

    public static void sendEmail(String from, String to, String subject, String text) throws MessagingException
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "25");

        Session session = Session.getDefaultInstance(props);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);

        Transport.send(message);
    }
    
    public static void sendEmail(String to, String subject, String text) throws MessagingException
    {
        sendEmail(NOREPLY_RURANOBE_EMAIL, to, subject, text);
    }
    
    public static boolean isEmailSyntaxValid(String email)
    {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    private static final String EMAIL_REGEXP = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEXP);
    private static final String NOREPLY_RURANOBE_EMAIL = "noreply@ruranobe.ru";
}
