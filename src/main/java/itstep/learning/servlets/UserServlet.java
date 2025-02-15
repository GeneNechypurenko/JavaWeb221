package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.User;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Singleton
public class UserServlet extends HttpServlet {

    private final DataContext dataContext;
    private final RestService restService;

    @Inject
    public UserServlet(DataContext dataContext, RestService restService) {
        this.dataContext = dataContext;
        this.restService = restService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        RestResponse restResponse = new RestResponse()
                .setResourceUrl("GET /user")
                .setCacheTime(600)
                .setMetadata(Map.of(
                        "dataType", "object",
                        "read", "GET /user",
                        "update", "PUT /user",
                        "delete", "DELETE /user"
                ));
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null) {
            restService.sendJson(resp, restResponse.setStatus(401).setData("Authorization header required"));
            return;
        }

        String authScheme = "Basic ";

        if (!authHeader.startsWith(authScheme)) {
            restService.sendJson(resp, restResponse.setStatus(401).setData("Authorization scheme error"));
            return;
        }

        String credentials = authHeader.substring(authScheme.length());

        try {
            credentials = new String(Base64.getDecoder().decode(credentials.getBytes()));
        } catch (Exception e) {
            restService.sendJson(resp, restResponse.setStatus(422).setData("Decode error: " + e.getMessage()));
            return;
        }

        String[] parts = credentials.split(":", 2);

        if(parts.length != 2) {
            restService.sendJson(resp, restResponse.setStatus(422).setData("Format error splitting by ':'"));
            return;
        }

        User user = dataContext.getUserDao().authorize(parts[0], parts[1]);

        if(user == null) {
            restService.sendJson(resp, restResponse.setStatus(401).setData("Credentials rejected"));
            return;
        }

        restResponse.setStatus(200).setData(user);
        restService.sendJson(resp, restResponse);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        restService.setCorsHeader(resp);
    }
}
