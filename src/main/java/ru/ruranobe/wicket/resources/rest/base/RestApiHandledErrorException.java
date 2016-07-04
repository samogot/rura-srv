package ru.ruranobe.wicket.resources.rest.base;

public class RestApiHandledErrorException extends RuntimeException
{
    public int getHttpResponseCode()
    {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode)
    {
        this.httpResponseCode = httpResponseCode;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getDescription()
    {
        return description;
    }

    public RestApiHandledErrorException setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public RestApiHandledErrorException(int httpResponseCode, String error, String message, String description)
    {
        super(message);
        this.httpResponseCode = httpResponseCode;
        this.error = error;
        this.description = description;
    }

    public RestApiHandledErrorException(int httpResponseCode, String error, String message)
    {
        this(httpResponseCode, error, message, null);
    }

    public RestApiHandledErrorException(String error, String message)
    {
        this(500, error, message, null);
    }
    private int httpResponseCode;
    private String error;
    private String description;
}
