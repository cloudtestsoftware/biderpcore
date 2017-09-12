package cms.service.util;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class EncriptManager {
	static Log logger = LogFactory.getLog(EncriptManager.class);
	private static final String characterEncoding = "UTF-8";
	private static final String cipherTransformation = "AES";
	private static final String aesEncryptionAlgorithm = "AES";
	private static String key="XMzDdG4D03CKm2IxIWQw7g==";
	
    public static String generateKey(String text){
    	return(encrypt(text,key));
    }
    public static String encrypt(String text, String secretKey) {
        byte[] raw;
        String encryptedString;
        SecretKeySpec skeySpec;
        byte[] encryptText = text.getBytes();
        Cipher cipher;
        try {
            raw = Base64.decodeBase64(secretKey);
            skeySpec = new SecretKeySpec(raw, aesEncryptionAlgorithm);
            cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
        } 
        catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        return encryptedString;
    }

    public static String decrypt(String text, String secretKey) {
        Cipher cipher;
        String encryptedString;
        byte[] encryptText = null;
        byte[] raw;
        SecretKeySpec skeySpec;
        try {
            raw = Base64.decodeBase64(secretKey);
            skeySpec = new SecretKeySpec(raw, aesEncryptionAlgorithm);
            encryptText = Base64.decodeBase64(text);
            cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            encryptedString = new String(cipher.doFinal(encryptText));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        return encryptedString;
    }
    
   

    public static byte[] decryptBase64EncodedWithManagedIV(String encryptedText, String key) throws Exception {
        byte[] cipherText = Base64.decodeBase64(encryptedText.getBytes());
        byte[] keyBytes = Base64.decodeBase64(key.getBytes());
        return decryptWithManagedIV(cipherText, keyBytes);
    }

    public static byte[] decryptWithManagedIV(byte[] cipherText, byte[] key) throws Exception{
        byte[] initialVector = Arrays.copyOfRange(cipherText,0,16);
        byte[] trimmedCipherText = Arrays.copyOfRange(cipherText,16,cipherText.length); 
        return decrypt(trimmedCipherText, key, initialVector);
    }

    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector) throws Exception{
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    
}