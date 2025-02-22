package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import itstep.learning.services.db.DbService;
import itstep.learning.services.random.RandomService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.*;
import java.util.*;


// @WebServlet("/home")
@Singleton
public class HomeServlet extends HttpServlet {

    private final RandomService randomService;
    private final DbService dbService;
    private final DataContext dataContext;
    private final RestService restService;

    @Inject
    public HomeServlet(RandomService randomService, DbService dbService, DataContext dataContext, RestService restService) {
        this.randomService = randomService;
        this.dbService = dbService;
        this.dataContext = dataContext;
        this.restService = restService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String message;

        try {
            String sql = "SELECT CURRENT_TIMESTAMP";
            Statement statement = dbService.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            resultSet.next();
            message = resultSet.getString(1);
            resultSet.close();

        } catch (SQLException e) {
            message = e.getMessage();
        }

        String msg = dataContext.getUserDao().installTables()
                && dataContext.getAccessTokenDao().installTables()
                ? "Install OK" : "Install Failed";

        restService.sendJson(resp, new RestResponse()
                .setStatus(200)
                .setMessage(message
                        + "; Random Number: " + randomService.randomInt()
                        + "; Random String: " + randomService.randomString(10)
                        + "; Random File Name: " + randomService.randomFileName(10)
                        + "; Data Context: " + msg)
                .setMetadata(Map.of(
                        "dataType", "object",
                        "read", "GET /home",
                        "update", "PUT /home",
                        "delete", "DELETE")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RestResponse restResponse = new RestResponse()
                .setResourceUrl("POST /home")
                .setCacheTime(0);

        UserSignupFormModel model;

        try {
            model = restService.fromBody(req, UserSignupFormModel.class);
        } catch (Exception e) {
            restService.sendJson(resp, restResponse.setStatus(422).setMessage(e.getMessage()));
            return;
        }

        User user = dataContext.getUserDao().addUser(model);
        if (user == null) {
            restService.sendJson(resp, new RestResponse()
                    .setStatus(507)
                    .setMessage("Failed to insert user")
                    .setData(model)
            );
        } else {
            restService.sendJson(resp, new RestResponse()
                    .setStatus(201)
                    .setMessage("Created")
                    .setData(model)
            );
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        restService.setCorsHeader(resp);
    }
}