package ru.ruranobe.engine.files;

import org.apache.wicket.util.string.Strings;

import java.io.Serializable;

public class FileStorageService implements Serializable
{

/*    public String getLogin()
    {
        return login;
    }

    public String getPassword()
    {
        return password;
    }*/

    public String getServiceName()
    {
        return serviceName;
    }

    /*public List<String> getFileExtensions()
    {
        return fileExtensions;
    }*/

    public String getRefreshToken()
    {
        return refreshToken;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getClientSecret()
    {
        return clientSecret;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    protected FileStorageService()
    {

    }

    protected void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    protected void setClientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
    }

    protected void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    protected void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    protected void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }


/*    protected void setLogin(String login)
    {
        this.login = login;
    }

    protected void setPassword(String password)
    {
        this.password = password;
    }

    protected void setFileExtensions(List<String> fileExtensions)
    {
        this.fileExtensions = fileExtensions;
    }*/

    public static class Builder
    {
        public Builder()
        {
            fileStorageService = new FileStorageService();
        }

        public FileStorageService build()
        {
            String serviceName = fileStorageService.getServiceName();
            if (Strings.isEmpty(serviceName))
            {
                throw new IllegalArgumentException("ServiceName is required to build FileStorageService");
            }
            StorageService storageService = StorageService.resolve(serviceName);
            StorageService.initializeService(fileStorageService, storageService);
            return fileStorageService;
        }

        public Builder setServiceName(String serviceName)
        {
            fileStorageService.setServiceName(serviceName);
            return this;
        }

/*        public Builder setFileExtension(List<String> fileExtensions)
        {
            fileStorageService.setFileExtensions(fileExtensions);
            return this;
        }

        public Builder setLogin(String login)
        {
            fileStorageService.setLogin(login);
            return this;
        }

        public Builder setPassword(String password)
        {
            fileStorageService.setPassword(password);
            return this;
        }*/

        public Builder setRefreshToken(String refreshToken)
        {
            fileStorageService.setRefreshToken(refreshToken);
            return this;
        }

        public Builder setClientId(String clientId)
        {
            fileStorageService.setClientId(clientId);
            return this;
        }

        public Builder setClientSecret(String clientSecret)
        {
            fileStorageService.setClientSecret(clientSecret);
            return this;
        }

        public Builder setAccessToken(String accessToken)
        {
            fileStorageService.setAccessToken(accessToken);
            return this;
        }

        private final FileStorageService fileStorageService;
    }

    /*    private String login;
        private String password;*/
    private String serviceName;
    private String refreshToken;
    private String clientId;
    private String clientSecret;
    private String accessToken;
//    private List<String> fileExtensions;
}
