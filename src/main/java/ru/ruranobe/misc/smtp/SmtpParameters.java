package ru.ruranobe.misc.smtp;

import java.util.List;

public class SmtpParameters
{
    public SmtpParameters(String host, String address, int port, List<EmailMessageTemplate> messages)
    {
        this.host = host;
        this.address = address;
        this.port = port;
        this.messages = messages;
    }

    public String getHost()
    {
        return host;
    }

    public String getAddress()
    {
        return address;
    }

    public int getPort()
    {
        return port;
    }

    public List<EmailMessageTemplate> getMessages()
    {
        return messages;
    }

    private String host;
    private String address;
    private int port;
    private List<EmailMessageTemplate> messages;
}
