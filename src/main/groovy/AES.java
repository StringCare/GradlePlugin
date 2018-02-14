
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AES {

    private static final String CHARSET = "UTF-8";
    private static final String MSG_D_ALGORITHM = "SHA-1";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";


    private static SecretKey generateKey(String key) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(MSG_D_ALGORITHM);
		try {
            byte[] acd = Arrays.copyOf(digest.digest(key.getBytes(CHARSET)), 16);
            return new SecretKeySpec(acd, ALGORITHM);
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    return null;
    }
	
    public static String encrypt(String message, String key) throws Exception {
    	byte[] data = Base64.encodeBase64String(message.getBytes(CHARSET)).getBytes();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey(key));
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String value, String key) throws Exception {
        SecretKeySpec spec = new SecretKeySpec(generateKey(key).getEncoded(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, spec);
        return new String(cipher.doFinal(Base64.encodeBase64String(value.getBytes()).getBytes()), CHARSET);
    }

}