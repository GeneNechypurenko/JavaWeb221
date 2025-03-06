package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.storage.StorageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        if (fileId == null || fileId.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("fileId cannot be empty");
            return;
        }

        int dotPosition = fileId.lastIndexOf('.');
        if (dotPosition == -1 || dotPosition == fileId.length() - 1) {
            resp.setStatus(400);
            resp.getWriter().write("fileId must contain a valid extension");
            return;
        }

        String extension = fileId.substring(dotPosition);
        if (isBlacklistedExtension(extension)) {
            resp.setStatus(400);
            resp.getWriter().write("Invalid file extension");
            return;
        }

        try (InputStream reader = storageService.get(fileId)) {
            resp.setContentType(mimeByExtension(extension));

            OutputStream writer = resp.getOutputStream();
            byte[] buffer = new byte[131072];
            int length;

            while ((length = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, length);
            }

        } catch (IOException e) {
            resp.setStatus(404);
            resp.getWriter().write("File not found");
        }
    }

    private boolean isBlacklistedExtension(String extension) {
        String[] blacklistedExtensions = {".exe", ".php", ".py", ".cgi"};
        for (String blacklisted : blacklistedExtensions) {
            if (extension.equalsIgnoreCase(blacklisted)) {
                return true;
            }
        }
        return false;
    }

    private String mimeByExtension(String extension) {
        switch (extension.toLowerCase()) {
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

            case ".json":
                return "application/json";

            case ".pdf":
                return "application/pdf";

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
