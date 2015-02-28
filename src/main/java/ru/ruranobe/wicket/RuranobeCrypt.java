package ru.ruranobe.wicket;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.wicket.util.crypt.AbstractCrypt;
import org.apache.wicket.util.crypt.SunJceCrypt;
import org.apache.wicket.util.lang.Args;

/* For some reasons SunJceCrypt doesn't work properly, or at the very least
   the way I expect it to work. In this class the same crypto was method implemented. */
public class RuranobeCrypt extends AbstractCrypt
{

    /**
     * Iteration count used in combination with the salt to create the
     * encryption key.
     */
    private final static int COUNT = 17;
    /**
     * Name of the default encryption method
     */
    public static final String DEFAULT_CRYPT_METHOD = "PBEWithMD5AndDES";
    /**
     * Salt
     */
    public final static byte[] SALT =
    {
        (byte) 0x15, (byte) 0x8c, (byte) 0xa3, (byte) 0x4a,
        (byte) 0x66, (byte) 0x51, (byte) 0x2a, (byte) 0xbc
    };
    /**
     * The name of encryption method (cipher)
     */
    private final String cryptMethod;

    /**
     * Constructor
     */
    public RuranobeCrypt()
    {
        this(DEFAULT_CRYPT_METHOD);
    }

    /**
     * Constructor that uses a custom encryption method (cipher). You may need
     * to override {@link #createKeySpec()} and/or
     * {@link #createParameterSpec()} for the custom cipher.
     *
     * @param cryptMethod the name of encryption method (the cipher)
     */
    public RuranobeCrypt(String cryptMethod)
    {
        this.cryptMethod = Args.notNull(cryptMethod, "Crypt method");

        if (Security.getProviders("Cipher." + cryptMethod).length > 0)
        {
            return; // we are good to go!
        }
        try
        {
            // Initialize and add a security provider required for encryption
            final Class<?> clazz = Class.forName("com.sun.crypto.provider.SunJCE");

            Security.addProvider((Provider) clazz.newInstance());
        } catch (Exception ex)
        {
            throw new RuntimeException("Unable to load SunJCE service provider", ex);
        }
    }

    /**
     * Crypts the given byte array
     *
     * @param input byte array to be encrypted
     * @param mode crypt mode
     * @return the input crypted. Null in case of an error
     * @throws GeneralSecurityException
     */
    @Override
    protected byte[] crypt(final byte[] input, final int mode)
            throws GeneralSecurityException
    {
        

       /* Base64 encoder = new Base64();
        java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
        
        PBEParameterSpec ps = new javax.crypto.spec.PBEParameterSpec(SALT, COUNT);
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey k = generateSecretKey();
        
        Cipher encryptCipher = Cipher.getInstance("PBEWithMD5AndDES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, k, ps);
        byte[]enc = null;
        try
        {
        byte[] utf81 = "Keiko-sep-8df2832ab08e5eee4af32875fc4e89ba".getBytes("UTF8");
        enc = encryptCipher.doFinal(utf81);
        } catch (Exception ex){}
        
      
        byte[] encoded=(new Base64()).encode(enc);*/
        
        SecretKey key = generateSecretKey();
        AlgorithmParameterSpec spec = createParameterSpec();
        Cipher ciph = createCipher(key, spec, mode);
/*
        if (1==1)
        {
            throw new RuntimeException(new String(Base64.encodeBase64(ciph.doFinal(input))));
        }*/
        return ciph.doFinal(input);
    }

    /**
     * Creates the {@link javax.crypto.Cipher} that will do the de-/encryption.
     *
     * @param key the secret key to use
     * @param spec the parameters spec to use
     * @param mode the mode ({@link javax.crypto.Cipher#ENCRYPT_MODE} or {@link javax.crypto.Cipher#DECRYPT_MODE})
     * @return the cipher that will do the de-/encryption
     * @throws GeneralSecurityException
     */
    protected Cipher createCipher(SecretKey key, AlgorithmParameterSpec spec, int mode) throws GeneralSecurityException
    {
        Cipher cipher = Cipher.getInstance(cryptMethod);
        cipher.init(mode, key, spec);
        return cipher;
    }

    /**
     * Generate the de-/encryption key. <p> Note: if you don't provide your own
     * encryption key, the implementation will use a default. Be aware that this
     * is potential security risk. Thus make sure you always provide your own
     * one.
     *
     * @return secretKey the security key generated
     * @throws NoSuchAlgorithmException unable to find encryption algorithm
     * specified
     * @throws InvalidKeySpecException invalid encryption key
     */
    protected SecretKey generateSecretKey() throws NoSuchAlgorithmException,
            InvalidKeySpecException
    {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptMethod);
        KeySpec spec = createKeySpec();
        return keyFactory.generateSecret(spec);
    }

    /**
     * @return the parameter spec to be used for the configured crypt method
     */
    protected AlgorithmParameterSpec createParameterSpec()
    { 
        return new PBEParameterSpec(SALT, COUNT);
    }

    /**
     * @return the key spec to be used for the configured crypt method
     */
    protected KeySpec createKeySpec()
    {
        return new PBEKeySpec(getKey().toCharArray());
    }

    
    /*
    @Override
    protected byte[] crypt(byte[] input, int mode) throws GeneralSecurityException
    {
        if (1==1)
        {
            throw new RuntimeException(new String(input));
        }
        
        Base64 encoder = new Base64();
        java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
        
        PBEParameterSpec ps = new javax.crypto.spec.PBEParameterSpec(SALT, COUNT);
        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey k = kf.generateSecret(new PBEKeySpec(getKey().toCharArray()));
        
        Cipher encryptCipher = Cipher.getInstance("PBEWithMD5AndDES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, k, ps);
        
        /*byte[] utf81 = input.getBytes("UTF8");
        byte[] enc = encryptCipher.doFinal(utf81);
        byte[] encoded=(new Base64()).encode(enc);*/
        
      /*  return input;
    }

    private final static int COUNT = 17;
    
    private final static byte[] SALT = { (byte)0x15, (byte)0x8c, (byte)0xa3, (byte)0x4a,
			(byte)0x66, (byte)0x51, (byte)0x2a, (byte)0xbc };
*/
}
