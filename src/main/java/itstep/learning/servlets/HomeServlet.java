package itstep.learning.servlets;

import itstep.learning.rest.RestResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.*;

import com.google.gson.Gson;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final Gson gson = new Gson();

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

        } catch (SQLException e) {
            message = e.getMessage();
        }

        resp.getWriter().print(gson.toJson(new RestResponse().setStatus(200).setMessage(message)));
    }
}