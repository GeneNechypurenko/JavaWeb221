package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.storage.StorageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Singleton
public class StorageServlet extends HttpServlet {
    private final StorageService storageService;

    @Inject
    public StorageServlet(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileId = req.getPathInfo().substring(1);

        try (InputStream reader = storageService.get(fileId)) {

            int dotPosition = fileId.lastIndexOf('.');
            String extension = fileId.substring(dotPosition);
            resp.setContentType(mimeByExtension(extension));

            OutputStream writer = resp.getOutputStream();
            byte[] buffer = new byte[131072];
            int length;

            while ((length = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, length);
            }

        } catch (IOException e) {
            resp.setStatus(404);
        }
    }

    private String mimeByExtension(String extension) {
        switch (extension) {

            case ".jpg":
                extension = ".jpeg";
            case ".jpeg":
            case ".png":
            case ".bmp":
            case ".gif":
            case ".webp":
                return "image/" + extension.substring(1);

            case ".txt":
                extension = ".plain";
            case ".css":
            case ".csv":
            case ".html":
                return "text/" + extension.substring(1);

            case ".js":
            case ".mjs":
                return "text/javascript";

            default:
                return "application/octet-stream";
        }
    }
}

//        http://localhost:8080/Java-Web-221/storage/123?x=10&y=20
//        req.getMethod()                GET
//        req.getRequestURI()            /Java-Web-221/storage/123
//        req.getContextPath()           /Java-Web-221
//        req.getServletPath()           /storage
//        req.getPathInfo()              /123
//        req.getQueryString()           x=10&y=20
