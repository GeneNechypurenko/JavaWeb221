package itstep.learning.rest;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestService {

    private final Gson gson = new Gson();

    public void sendJson(HttpServletResponse resp, RestResponse restResponse) throws IOException {
        resp.setContentType("application/json");
        setCorsHeader(resp);
        resp.getWriter().print(gson.toJson(restResponse));
    }

    public void setCorsHeader(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Headers", "content-type, authorization");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
       return gson.fromJson(json, classOfT);
    }

    public <T> T fromBody(HttpServletRequest req, Class<T> classOfT) throws IOException {
        String charsetName = req.getCharacterEncoding();
        if(charsetName == null) {
            charsetName = StandardCharsets.UTF_8.name();
        }
        return gson.fromJson(new String(req.getInputStream().readAllBytes(), charsetName), classOfT);
    }
}
