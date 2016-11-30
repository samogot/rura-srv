package ru.ruranobe.wicket.components;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import java.util.Locale;

public class CreditCardConverter implements IConverter<String>
{
    @Override
    public String convertToObject(String s, Locale locale) throws ConversionException
    {
        return s.replaceAll("\\D*", "");
    }

    @Override
    public String convertToString(String cardDigts, Locale locale)
    {
        switch (cardDigts.length())
        {
            case 12:
                return cardDigts.replaceAll("(\\d{4})(\\d{4})(\\d{4})", "$1 $2 $3");
            case 13:
                return cardDigts.replaceAll("(\\d{4})(\\d{5})(\\d{4})", "$1 $2 $3");
            case 14:
                return cardDigts.replaceAll("(\\d{4})(\\d{6})(\\d{4})", "$1 $2 $3");
            case 15:
                return cardDigts.replaceAll("(\\d{4})(\\d{6})(\\d{5})", "$1 $2 $3");
            case 16:
                return cardDigts.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1 $2 $3 $4");
            case 18:
                return cardDigts.replaceAll("(\\d{8})(\\d{10})", "$1 $2");
            case 19:
                return cardDigts.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{3})", "$1 $2 $3 $4 $5");
            default:
                return cardDigts;
        }
    }
}