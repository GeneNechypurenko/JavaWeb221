package itstep.learning.services.random;

public interface RandomService {
    int randomInt();
    String randomString(int length);
    String randomFileName(int length);
    String generateRandomStringByType(String type, int length);
}
