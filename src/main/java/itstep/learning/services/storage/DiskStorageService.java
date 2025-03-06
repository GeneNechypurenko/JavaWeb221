package itstep.learning.services.storage;

import com.google.inject.Singleton;

import java.io.*;
import java.util.UUID;

@Singleton
public class DiskStorageService implements StorageService {

    private final String path = "D:/Work/repos/Java/Storage/java211/";

    @Override
    public String put(InputStream inputStream, String extension) throws IOException {

        String fileId = UUID.randomUUID() + extension;
        File file = new File(path + fileId);

        FileOutputStream writer = new FileOutputStream(file);
        byte[] buffer = new byte[131072];
        int length;

        while ((length = inputStream.read(buffer)) > 0) {
            writer.write(buffer, 0, length);
        }

        writer.close();

        return fileId;
    }

    @Override
    public InputStream get(String itemId) throws IOException {
        return new FileInputStream(path + itemId);
    }
}
