package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.Product;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
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
import java.util.Map;
import java.util.UUID;

@Singleton
public class ProductServlet extends HttpServlet {

    private final FormParseService formParseService;
    private final StorageService storageService;
    private final DataContext dataContext;
    private final RestService restService;

    @Inject
    public ProductServlet(FormParseService formParseService, StorageService storageService, DataContext dataContext, RestService restService) {
        this.formParseService = formParseService;
        this.storageService = storageService;
        this.dataContext = dataContext;
        this.restService = restService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            restService.sendJson(resp, new RestResponse().setStatus(401).setData("Unauthorized: Missing or invalid token"));
            return;
        }
        String accessTokenId = authHeader.substring(7);

        FormParseResult formParseResult = formParseService.parseRequest(req);

        RestResponse restResponse = new RestResponse()
                .setResourceUrl("POST /product")
                .setMetadata(Map.of("dataType", "object"));

        Product product = new Product();
        String str;

        str = formParseResult.getFields().get("product-title");
        if (str == null || str.isEmpty()) {
            restService.sendJson(resp, restResponse.setStatus(400).setData("Missing required parameter 'product-title'"));
            return;
        }
        product.setProductTitle(str);

        str = formParseResult.getFields().get("product-description");
        if (str == null || str.isEmpty()) {
            restService.sendJson(resp, restResponse.setStatus(400).setData("Missing required parameter 'product-description'"));
            return;
        }
        product.setProductDescription(str);

        str = formParseResult.getFields().get("product-code");
        product.setProductSlug(str);

        try {
            product.setProductPrice(Double.parseDouble(formParseResult.getFields().get("product-price")));
        } catch (NumberFormatException | NullPointerException e) {
            restService.sendJson(resp, restResponse.setStatus(400).setData("Data parse error 'product-price': " + e.getMessage()));
            return;
        }

        try {
            product.setProductStock(Integer.parseInt(formParseResult.getFields().get("product-stock")));
        } catch (NumberFormatException | NullPointerException e) {
            restService.sendJson(resp, restResponse.setStatus(400).setData("Data parse error 'product-stock': " + e.getMessage()));
            return;
        }

        try {
            product.setCategoryId(UUID.fromString(formParseResult.getFields().get("category-id")));
        } catch (IllegalArgumentException | NullPointerException e) {
            restService.sendJson(resp, restResponse.setStatus(400).setData("Data parse error 'category-id': " + e.getMessage()));
            return;
        }

        FileItem image = formParseResult.getFiles().get("product-image");
        String savedFileId = null;
        if (image.getSize() > 0) {
            int dotPosition = image.getName().lastIndexOf(".");
            String ext = (dotPosition >= 0) ? image.getName().substring(dotPosition) : "";
            savedFileId = storageService.put(image.getInputStream(), ext);
        }
        product.setProductImageId(savedFileId);

        product = dataContext.getProductDao().addNewProduct(product);
        if (product == null) {
            if (savedFileId != null) {
                storageService.delete(savedFileId);
            }
            restService.sendJson(resp, restResponse.setStatus(500).setData("Internal Server Error. See logs for details"));
            return;
        }

        restService.sendJson(resp, restResponse.setStatus(200).setData(product));
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

    private void getCategories(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        restService.sendJson(resp, new RestResponse()
                .setResourceUrl("GET /product?type=categories")
                .setMetadata(Map.of("dataType", "array"))
                .setStatus(200)
                .setCacheTime(86400)
                .setData(dataContext.getCategoryDao().getList())
        );
    }

    private void getProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        restService.setCorsHeader(resp);
    }
}
