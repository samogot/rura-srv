package ru.ruranobe.mybatis.entities.tables;

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
    private Integer passVersion;
    private Date passRecoveryTokenDate;
    private String email;
    private String emailToken;
    private Date emailTokenDate;
    private boolean emailActivated;
    private Date registrationDate;
    private String converterType;
    private String navigationType;
    private boolean convertWithImgs;
    private boolean adult;
    private boolean preferColoredImgs;
    private Integer convertImgsSize;

    public User()
    {

    }

    public User(User user)
    {
        this.userId = user.userId;
        this.username = user.username;
        this.realname = user.realname;
        this.pass = user.pass;
        this.passRecoveryToken = user.passRecoveryToken;
        this.passVersion = user.passVersion;
        this.passRecoveryTokenDate = user.passRecoveryTokenDate;
        this.email = user.email;
        this.emailToken = user.emailToken;
        this.emailTokenDate = user.emailTokenDate;
        this.emailActivated = user.emailActivated;
        this.registrationDate = user.registrationDate;
        this.converterType = user.converterType;
        this.navigationType = user.navigationType;
        this.convertWithImgs = user.convertWithImgs;
        this.adult = user.adult;
        this.preferColoredImgs = user.preferColoredImgs;
        this.convertImgsSize = user.convertImgsSize;
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

    public Integer getPassVersion() {
        return passVersion;
    }

    public void setPassVersion(Integer passVersion) {
        this.passVersion = passVersion;
    }

    public String getConverterType() {
        return converterType;
    }

    public void setConverterType(String converterType) {
        this.converterType = converterType;
    }

    public String getNavigationType() {
        return navigationType;
    }

    public void setNavigationType(String navigationType) {
        this.navigationType = navigationType;
    }

    public boolean isConvertWithImgs() {
        return convertWithImgs;
    }

    public void setConvertWithImgs(boolean convertWithImgs) {
        this.convertWithImgs = convertWithImgs;
    }

    public boolean isPreferColoredImgs() {
        return preferColoredImgs;
    }

    public void setPreferColoredImgs(boolean preferColoredImgs) {
        this.preferColoredImgs = preferColoredImgs;
    }

    public Integer getConvertImgsSize() {
        return convertImgsSize;
    }

    public void setConvertImgsSize(Integer convertImgsSize) {
        this.convertImgsSize = convertImgsSize;
    }
}
