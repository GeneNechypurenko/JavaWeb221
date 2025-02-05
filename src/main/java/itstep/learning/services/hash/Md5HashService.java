package itstep.learning.services.hash;

import com.google.inject.Singleton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Singleton
public class Md5HashService implements HashService {

    @Override
    public String digest(String input) {

        try {
            char[] chars = new char[32];
            int i = 0;

            for (byte b : MessageDigest.getInstance("MD5").digest(input.getBytes())) {

                int bi = b & 0xff;
                String hs = Integer.toHexString(bi);

                if (bi < 16) {
                    chars[i] = '0';
                    chars[i + 1] = hs.charAt(0);
                } else {
                    chars[i] = hs.charAt(0);
                    chars[i + 1] = hs.charAt(1);
                }
                i += 2;
            }
            return new String(chars);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
