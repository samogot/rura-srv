package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;

/**
 * Created by samogot on 25.11.16.
 */
public class Requisite implements Serializable
{

    private static final long serialVersionUID = 2L;
    private Integer requisiteId;
    private String title;
    private String qiwi;
    private String wmr;
    private String wmu;
    private String wmz;
    private String wme;
    private String wmb;
    private String wmg;
    private String wmk;
    private String wmx;
    private String yandex;
    private String paypal;
    private String card;
    private String bitcoin;

    public Integer getRequisiteId()
    {
        return requisiteId;
    }

    public void setRequisiteId(Integer requisiteId)
    {
        this.requisiteId = requisiteId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getQiwi()
    {
        return qiwi;
    }

    public void setQiwi(String qiwi)
    {
        this.qiwi = qiwi;
    }

    public String getWmr()
    {
        return wmr;
    }

    public void setWmr(String wmr)
    {
        this.wmr = wmr;
    }

    public String getWmu()
    {
        return wmu;
    }

    public void setWmu(String wmu)
    {
        this.wmu = wmu;
    }

    public String getWmz()
    {
        return wmz;
    }

    public void setWmz(String wmz)
    {
        this.wmz = wmz;
    }

    public String getWme()
    {
        return wme;
    }

    public void setWme(String wme)
    {
        this.wme = wme;
    }

    public String getWmb()
    {
        return wmb;
    }

    public void setWmb(String wmb)
    {
        this.wmb = wmb;
    }

    public String getWmg()
    {
        return wmg;
    }

    public void setWmg(String wmg)
    {
        this.wmg = wmg;
    }

    public String getWmk()
    {
        return wmk;
    }

    public void setWmk(String wmk)
    {
        this.wmk = wmk;
    }

    public String getWmx()
    {
        return wmx;
    }

    public void setWmx(String wmx)
    {
        this.wmx = wmx;
    }

    public String getYandex()
    {
        return yandex;
    }

    public void setYandex(String yandex)
    {
        this.yandex = yandex;
    }

    public String getPaypal()
    {
        return paypal;
    }

    public void setPaypal(String paypal)
    {
        this.paypal = paypal;
    }

    public String getCard()
    {
        return card;
    }

    public void setCard(String card)
    {
        this.card = card;
    }

    public String getBitcoin()
    {
        return bitcoin;
    }

    public void setBitcoin(String bitcoin)
    {
        this.bitcoin = bitcoin;
    }
}
