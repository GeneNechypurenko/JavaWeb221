package itstep.learning.services.random;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.datetime.DateTimeService;
import java.util.Random;

@Singleton
public class UtilRandomService implements RandomService {
    private final Random rand;
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String FILESAFE_ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

    @Inject
    public UtilRandomService(DateTimeService dateTimeService) {
        this.rand = new Random(dateTimeService.getTimestamp());
    }

    @Override
    public int randomInt() {
        return rand.nextInt();
    }

    @Override
    public String randomString(int length) {
        return generateRandomString(length, ALPHANUMERIC);
    }

    @Override
    public String randomFileName(int length) {
        return generateRandomString(length, FILESAFE_ALPHANUMERIC);
    }

    private String generateRandomString(int length, String charset) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charset.charAt(rand.nextInt(charset.length())));
        }
        return sb.toString();
    }

    @Override
    public String generateRandomStringByType(String type, int length) {
        return switch (type.toLowerCase()) {
            case "salt" -> randomString(length);
            case "filename" -> randomFileName(length);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}
