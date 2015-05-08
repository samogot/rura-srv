package ru.ruranobe.mybatis.tables;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable
{
    private static final long serialVersionUID = 2L;
    private Integer userId;
    private String username;
    private String realname;
    private String pass;
    private String passRecoveryToken;
    private Date passRecoveryTokenDate;
    private String email;
    private String emailToken;
    private Date emailTokenDate;
    private boolean emailActivated;
    private Date registrationDate;
    private boolean adult;

    public User()
    {

    }

    public User(String username, String realname, String pass,
                String passRecoveryToken, Date passRecoveryTokenDate, String email,
                String emailToken, Date emailTokenDate, boolean isEmailActivated, Date registrationDate)
    {
        this.username = username;
        this.realname = realname;
        this.pass = pass;
        this.passRecoveryToken = passRecoveryToken;
        this.passRecoveryTokenDate = passRecoveryTokenDate;
        this.email = email;
        this.emailToken = emailToken;
        this.emailTokenDate = emailTokenDate;
        this.emailActivated = isEmailActivated;
        this.registrationDate = registrationDate;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getRealname()
    {
        return realname;
    }

    public void setRealname(String realname)
    {
        this.realname = realname;
    }

    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate)
    {
        this.registrationDate = registrationDate;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmailToken()
    {
        return emailToken;
    }

    public void setEmailToken(String emailToken)
    {
        this.emailToken = emailToken;
    }

    public Date getEmailTokenDate()
    {
        return emailTokenDate;
    }

    public void setEmailTokenDate(Date emailTokenDate)
    {
        this.emailTokenDate = emailTokenDate;
    }

    public boolean isEmailActivated()
    {
        return emailActivated;
    }

    public void setEmailActivated(boolean emailActivated)
    {
        this.emailActivated = emailActivated;
    }

    public String getPass()
    {
        return pass;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public String getPassRecoveryToken()
    {
        return passRecoveryToken;
    }

    public void setPassRecoveryToken(String passRecoveryToken)
    {
        this.passRecoveryToken = passRecoveryToken;
    }

    public Date getPassRecoveryTokenDate()
    {
        return passRecoveryTokenDate;
    }

    public void setPassRecoveryTokenDate(Date passRecoveryTokenDate)
    {
        this.passRecoveryTokenDate = passRecoveryTokenDate;
    }

    public boolean isAdult()
    {
        return adult;
    }

    public void setAdult(boolean adult)
    {
        this.adult = adult;
    }
}
