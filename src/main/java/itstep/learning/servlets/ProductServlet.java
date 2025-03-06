package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.form_parse.FormParseResult;
import itstep.learning.services.form_parse.FormParseService;
import itstep.learning.services.storage.StorageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload2.core.FileItem;

import java.io.IOException;
import java.nio.file.Paths;

@Singleton
public class ProductServlet extends HttpServlet {

    private final FormParseService formParseService;
    private final StorageService storageService;

    @Inject
    public ProductServlet(FormParseService formParseService, StorageService storageService) {
        this.formParseService = formParseService;
        this.storageService = storageService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FormParseResult formParseResult = formParseService.parseRequest(req);
        resp.getWriter().print(
                String.join(" ", formParseResult.getFiles().keySet()) + " " +
                        String.join(" ", formParseResult.getFields().keySet())
        );

        FileItem file1 = formParseResult.getFiles().get("file1");
        String message;
        if(file1.getSize() > 0){
            int dotPosition = file1.getName().lastIndexOf(".");
            String extension = file1.getName().substring(dotPosition);
            String fileId = storageService.put(file1.getInputStream(), extension);
            message = fileId;
        }else{
            message = "No file submitted";
        }
        resp.getWriter().println(message);

//        FileItem picture = formParseResult.getFiles().get("picture");
//        String name = formParseResult.getFields().get("name");
//        String price = formParseResult.getFields().get("price");
//        String description = formParseResult.getFields().get("description");
//        String code = formParseResult.getFields().get("code");
//        String stock = formParseResult.getFields().get("stock");
//        String categoryId = formParseResult.getFields().get("categoryId");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        if ("categories".equals(type)) {         //  product?type=categories
            getCategories(req, resp);
        } else if ("category".equals(type)) {      //  product?type=category&id=....
            getCategory(req, resp);
        } else if ("product".equals(type)) {
            getProducts(req, resp);
        }
    }

    private void getCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    }

    private void getCategories(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    }

    private void getProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    }
}
