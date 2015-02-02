package ru.ruranobe.wicket;

import java.util.Properties;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email 
{

    public static void sendEmail(String to, String subject, String text) throws MessagingException
    {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, 
                new javax.mail.Authenticator() 
                {
                    protected PasswordAuthentication getPasswordAuthentication() 
                    {
                        return new PasswordAuthentication(RURANOBE_EMAIL, RURANOBE_EMAIL_PASSWORD);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(RURANOBE_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(text);

        Transport.send(message);
    }
    
    public static boolean validateEmailSyntax(String email)
    {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    private static final String EMAIL_REGEXP = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEXP);
    private static final String RURANOBE_EMAIL = "ruranobe@gmail.com";
    private static final String RURANOBE_EMAIL_PASSWORD = "password";
}
