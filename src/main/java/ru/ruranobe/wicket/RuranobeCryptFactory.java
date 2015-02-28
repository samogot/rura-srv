package ru.ruranobe.wicket;

import org.apache.wicket.util.crypt.ClassCryptFactory;
import org.apache.wicket.util.crypt.CryptFactoryCachingDecorator;

public class RuranobeCryptFactory extends CryptFactoryCachingDecorator
{

    public RuranobeCryptFactory(final String encryptionKey)
    {
        super(new ClassCryptFactory(RuranobeCrypt.class, encryptionKey));
    }
}
