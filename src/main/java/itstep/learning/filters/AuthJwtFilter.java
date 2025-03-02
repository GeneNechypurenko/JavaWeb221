package itstep.learning.filters;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.services.hash.HashService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Base64;

@Singleton
public class AuthJwtFilter implements Filter {

    private FilterConfig filterConfig;
    private final HashService hashService;

    @Inject
    public AuthJwtFilter(HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain next)
            throws IOException, ServletException {

        checkJwtToken((HttpServletRequest) req);

        next.doFilter(req, resp);
    }

    private void checkJwtToken(HttpServletRequest request) {

        String secret = "secret";

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            request.setAttribute("authStatus", "Authorization header required");
            return;
        }

        String authScheme = "Bearer ";
        if (!authHeader.startsWith(authScheme)) {
            request.setAttribute("authStatus", "Authorization scheme invalid");
            return;
        }

        String credentials = authHeader.substring(authScheme.length());
        String[] parts = credentials.split("\\.");
        if (parts.length != 3) {
            request.setAttribute("authStatus", "Authorization token invalid");
            return;
        }

        String header = parts[0];
        String payload = parts[1];
        String signature = new String(Base64.getUrlDecoder().decode(parts[2]));

        if(!signature.equals(hashService.digest(secret + header + "." + payload))) {
            request.setAttribute("authStatus", "Token signature error");
            return;
        }

        payload = new String(Base64.getUrlDecoder().decode(payload));
        UserAccess userAccess = new Gson().fromJson(payload, UserAccess.class);
        request.setAttribute("authStatus", "OK");
        request.setAttribute("authUserAccess", userAccess);
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
