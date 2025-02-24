package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.User;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
public class UserServlet extends HttpServlet {

    private final DataContext dataContext;
    private final RestService restService;
    private final Logger logger;

    @Inject
    public UserServlet(DataContext dataContext, RestService restService, Logger logger) {
        this.dataContext = dataContext;
        this.restService = restService;
        this.logger = logger;
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

        if (parts.length != 2) {
            restService.sendJson(resp, restResponse.setStatus(422).setData("Format error splitting by ':'"));
            return;
        }

        User user = dataContext.getUserDao().authorize(parts[0], parts[1]);

        if (user == null) {
            restService.sendJson(resp, restResponse.setStatus(401).setData("Credentials rejected"));
            return;
        }

        restResponse.setStatus(200).setData(user);
        restService.sendJson(resp, restResponse);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RestResponse restResponse =
                new RestResponse()
                        .setResourceUrl("PUT /user")
                        .setMetadata(Map.of(
                                "dataType", "object",
                                "read", "GET /user",
                                "update", "PUT /user",
                                "delete", "DELETE /user"
                        ));

        User userUpdated;

        try {
            userUpdated = restService.fromBody(req, User.class);
        } catch (Exception e) {
            restService.sendJson(resp, restResponse.setStatus(422).setMessage(e.getMessage()));
            return;
        }

        if (userUpdated == null || userUpdated.getUserId() == null) {
            restService.sendJson(resp, restResponse.setStatus(422).setMessage("Unparseable data or identity undefined"));
            return;
        }

        User user = dataContext.getUserDao().getUserById(userUpdated.getUserId());

        if (user == null) {
            restService.sendJson(resp, restResponse.setStatus(404).setMessage("User not found"));
            return;
        }

        if (!dataContext.getUserDao().updateUserAsync(userUpdated).join()) {
            restService.sendJson(resp, restResponse.setStatus(500).setMessage("Server error. See logs for details"));
            return;
        }

        restResponse.setStatus(202).setData(userUpdated).setCacheTime(0);
        restService.sendJson(resp, restResponse);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RestResponse restResponse =
                new RestResponse()
                        .setResourceUrl("DELETE /user")
                        .setMetadata(Map.of(
                                "dataType", "object",
                                "read", "GET /user",
                                "update", "PUT /user",
                                "delete", "DELETE /user"
                        ));

        String userId = req.getParameter("id");

        if (userId == null) {
            restService.sendJson(resp, restResponse.setStatus(400).setMessage("Missing required ID"));
            return;
        }

        UUID userUuid;

        try {
            userUuid = UUID.fromString(userId);
        } catch (Exception ignored) {
            restService.sendJson(resp, restResponse.setStatus(400).setMessage("Invalid ID format"));
            return;
        }

        User deletedUser = dataContext.getUserDao().getUserById(userUuid);

        if (deletedUser == null) {
            restService.sendJson(resp, restResponse.setStatus(401).setMessage("User not authorized"));
            return;
        }

        try {
            dataContext.getUserDao().deleteUserAsync(deletedUser);
        } catch (Exception e) {
            logger.warning("deleteUserAsync fail: " + e.getMessage());
            restService.sendJson(resp, restResponse.setStatus(500).setMessage("Server error"));
            return;
        }

        restResponse.setStatus(202).setData("Deleted").setCacheTime(0);
        restService.sendJson(resp, restResponse);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        restService.setCorsHeader(resp);
    }
}
