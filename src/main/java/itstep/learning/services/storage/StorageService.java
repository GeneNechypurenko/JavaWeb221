package itstep.learning.services.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    String put(InputStream inputStream, String extension) throws IOException;
    InputStream get(String itemId) throws IOException;
}
