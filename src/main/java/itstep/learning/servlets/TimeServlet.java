package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.datetime.DateTimeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Singleton
public class TimeServlet extends jakarta.servlet.http.HttpServlet {
    private final DateTimeService dateTimeService;

    @Inject
    public TimeServlet(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        long timestamp = dateTimeService.getTimestamp();
        String isoTime = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(timestamp));

        String displayTime = "Timestamp: " + timestamp + "\n" +
                "ISO Time: " + isoTime;

        resp.getWriter().print(displayTime);
    }
}
