package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.rest.RestResponse;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;
import itstep.learning.services.random.RandomService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import com.google.gson.Gson;

// @WebServlet("/home")
@Singleton
public class HomeServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final RandomService randomService;
    private final KdfService kdfService;
    private final DbService dbService;
    private final DataContext dataContext;

    @Inject
    public HomeServlet(RandomService randomService, KdfService kdfService, DbService dbService, DataContext dataContext) {
        this.randomService = randomService;
        this.kdfService = kdfService;
        this.dbService = dbService;
        this.dataContext = dataContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

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
        String msg = dataContext.getUserDao().installTables() ? "Install OK" : "Install Failed";
        sendJson(resp, new RestResponse()
                .setStatus(200)
                .setMessage(message + "; random number: " + randomService.randomInt()
                        + "; Data Context: " + msg)
                .setMetadata(Map.of(
                        "dataType", "object",
                        "read", "GET /home",
                        "update", "PUT /home",
                        "delete", "DELETE")));
    }

    private void sendJson(HttpServletResponse resp, RestResponse restResponse) throws IOException {

        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.getWriter().print(gson.toJson(restResponse));
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = new String(req.getInputStream().readAllBytes());

        RestResponse restResponse = new RestResponse()
                .setResourceUrl("POST /home")
                .setCacheTime(0);

        UserSignupFormModel model;

        try {
            model = gson.fromJson(body, UserSignupFormModel.class);
        } catch (Exception e) {
            sendJson(resp, restResponse.setStatus(422).setMessage(e.getMessage()));
            return;
        }

        User user = dataContext.getUserDao().addUser(model);
        if (user == null) {
            sendJson(resp, new RestResponse()
                    .setStatus(507)
                    .setMessage("Failed to insert user")
                    .setData(model)
            );
        } else {
            sendJson(resp, new RestResponse()
                    .setStatus(201)
                    .setMessage("Created")
                    .setData(model)
            );
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Headers", "*");
    }
}