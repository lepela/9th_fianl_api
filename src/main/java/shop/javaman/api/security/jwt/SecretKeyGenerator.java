package shop.javaman.api.security.jwt;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
  public static String genSecretKey() {
    byte[] key = new byte[32];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(key);

    String base64Key = Base64.getEncoder().encodeToString(key);
    return base64Key;
  }
}
