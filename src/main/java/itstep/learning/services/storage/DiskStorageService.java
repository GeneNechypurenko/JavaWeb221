package itstep.learning.services.storage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.config.ConfigService;

import java.io.*;
import java.util.UUID;

@Singleton
public class DiskStorageService implements StorageService {
    private final String path;

    @Inject
    public DiskStorageService(ConfigService configService) {
        this.path = configService.getValue("storage:storagePath").getAsString();
    }

    @Override
    public String put(InputStream inputStream, String extension) throws IOException {
        String fileId = UUID.randomUUID() + extension;
        File file = new File(path + fileId);

        try (FileOutputStream writer = new FileOutputStream(file)) {
            byte[] buffer = new byte[131072];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                writer.write(buffer, 0, length);
            }
        }

        return fileId;
    }

    @Override
    public InputStream get(String itemId) throws IOException {
        return new FileInputStream(path + itemId);
    }

    @Override
    public boolean delete(String itemId) {
        File file = new File(path + itemId);
        return file.exists() && file.delete();
    }
}
