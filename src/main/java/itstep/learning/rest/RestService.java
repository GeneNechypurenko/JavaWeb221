package itstep.learning.rest;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
       return gson.fromJson(json, classOfT);
    }
}
