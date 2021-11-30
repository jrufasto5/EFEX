package atento.ripley.util;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import atento.ripley.VariablesGlobales;

public class CifradoUtil {
	private static final String ALGORITHM = "AES";

	private static Logger log = Logger.getLogger(CifradoUtil.class.getName());

	 public static String encrypt(String valueToEnc) throws Exception {
		try {
			log.info("Comienza encrypt");
			Key key = generateKey();
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encValue = c.doFinal(valueToEnc.getBytes());
			valueToEnc = java.util.Base64.getEncoder().encodeToString(encValue);
			log.info("Finaliza encrypt");
		} catch (Exception e) {
			log.error("Error encrypt");
			log.error(e);
		}
		return valueToEnc;
	}

	public static String decrypt(String encryptedValue) throws Exception {
		try {
			log.info("Comienza decrypt");
			Key key = generateKey();
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decordedValue = java.util.Base64.getDecoder().decode(encryptedValue);
			byte[] decValue = c.doFinal(decordedValue);
			encryptedValue = new String(decValue);
			log.info("Finaliza decrypt");
			
		} catch (Exception e) {
			log.error("Error decrypt");
			log.error(e);
		}
		return encryptedValue;
	}

	private static Key generateKey() throws Exception {
	    Key key = null;
		try {
			key = new SecretKeySpec(VariablesGlobales.CIFRADO_FRASE.getBytes(), ALGORITHM);
		} catch (Exception e) {
			log.error("Error generateKey");
			log.error(e);
		}
	    return key;
	}
}
      