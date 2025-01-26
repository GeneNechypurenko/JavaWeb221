package itstep.learning.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/time")
public class TimeServlet extends jakarta.servlet.http.HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        long timestamp = System.currentTimeMillis();
        String isoTime = java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochMilli(timestamp));

        String displayTime = "Timestamp: " + timestamp + "\n" +
                             "ISO Time: " + isoTime;

        resp.getWriter().print(displayTime);
    }
}
