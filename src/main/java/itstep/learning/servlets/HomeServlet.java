package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.rest.RestResponse;
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

    @Inject
    public HomeServlet(RandomService randomService) {
        this.randomService = randomService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String message;

        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            String connectionString = "jdbc:mysql://localhost:3308/java221";
            Connection connection = DriverManager.getConnection(
                    connectionString,
                    "user221",
                    "pass221");

            String sql = "SELECT CURRENT_TIMESTAMP";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            resultSet.next();
            message = resultSet.getString(1);
            resultSet.close();

        } catch (SQLException e) {
            message = e.getMessage();
        }
        sendJson(resp, new RestResponse().setStatus(200).setMessage(message + "; random int: " + randomService.randomInt()));
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

        sendJson(resp, new RestResponse()
                .setStatus(201)
                .setMessage("Created")
                .setMetadata(Map.of(
                        "dataType", "object",
                        "read", "GET /home",
                        "update", "PUT /home",
                        "delete", "DELETE"))
                .setData(model)
        );
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Headers", "*");
    }
}