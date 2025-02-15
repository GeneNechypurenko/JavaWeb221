package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import itstep.learning.services.random.RandomService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@Singleton
public class RandomServlet extends HttpServlet {
    private final RandomService randomService;
    private final RestService restService;

    @Inject
    public RandomServlet(RandomService randomService, RestService restService) {
        this.randomService = randomService;
        this.restService = restService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String type = req.getParameter("type");
        String lengthStr = req.getParameter("length");

        if (type == null || lengthStr == null) {
            restService.sendJson(resp, new RestResponse()
                    .setStatus(400)
                    .setMessage("Missing required parameters: type and length"));
            return;
        }

        int length;
        try {
            length = Integer.parseInt(lengthStr);
            if (length <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            restService.sendJson(resp, new RestResponse()
                    .setStatus(400)
                    .setMessage("Invalid length parameter"));
            return;
        }

        try {
            String randomString = randomService.generateRandomStringByType(type, length);
            restService.sendJson(resp,
                    new RestResponse()
                            .setStatus(200)
                            .setResourceUrl("GET /random")
                            .setMetadata(Map.of(
                                    "dataType", "string",
                                    "read", "GET /random",
                                    "type", type,
                                    "length", String.valueOf(length)))
                            .setCacheTime(0)
                            .setData(randomString));
        } catch (IllegalArgumentException e) {
            restService.sendJson(resp, new RestResponse()
                    .setStatus(400)
                    .setMessage(e.getMessage()));
        }
    }
}
