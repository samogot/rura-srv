package ru.ruranobe.misc.smtp;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.regex.Pattern;

public class Email
{
    public static void initializeSmtp(SmtpParameters parameters)
    {
        host = parameters.getHost();
        address = parameters.getAddress();
        port = parameters.getPort();

        for (EmailMessageTemplate message: parameters.getMessages())
        {
            String messageId = message.getId();
            switch (messageId)
            {
                // Change email is implemented as EmailActivation message
                case "EmailActivation":
                    activateEmailSubject = message.getSubject();
                    activateEmailText = message.getText();
                    break;
                case "ChangePassword":
                    emailPasswordRecoverySubject = message.getSubject();
                    emailPasswordRecoveryText = message.getText();
                    break;
            }
        }
    }

    public static void sendPasswordRecoveryMessage(String to, String passwordRecoveryToken) throws MessagingException
    {
        send(to, emailPasswordRecoverySubject, String.format(emailPasswordRecoveryText, passwordRecoveryToken));
    }

    public static void sendEmailActivationMessage(String to, String emailToken) throws MessagingException
    {
        send(to, activateEmailSubject, String.format(activateEmailText, emailToken));
    }

    public static void send(String from, String to, String subject, String text) throws MessagingException
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getDefaultInstance(props);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);

        Transport.send(message);
    }

    public static void send(String to, String subject, String text) throws MessagingException
    {
        send(address, to, subject, text);
    }

    public static boolean isEmailSyntaxInvalid(String email)
    {
        return !EMAIL_PATTERN.matcher(email).matches();
    }

    private static String emailPasswordRecoverySubject;
    private static String emailPasswordRecoveryText;
    private static String activateEmailSubject;
    private static String activateEmailText;
    private static String host;
    private static String address;
    private static int port;

    public static final long ETERNITY_EXPIRATION_TIME = 31622400000000L;
    private static final String EMAIL_REGEXP = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEXP);
}
