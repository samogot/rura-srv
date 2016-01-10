package ru.ruranobe.misc.smtp;

public class EmailMessageTemplate
{
    public EmailMessageTemplate(String id, String subject, String text)
    {
        this.id = id;
        this.subject = subject;
        this.text = text;
    }

    public String getId()
    {
        return id;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getText()
    {
        return text;
    }

    private String id;
    private String subject;
    private String text;
}
