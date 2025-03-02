package itstep.learning.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.UserAccess;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Singleton
public class AuthFilter implements Filter {

    private FilterConfig filterConfig;
    private final DataContext dataContext;

    @Inject
    public AuthFilter(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain next)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        String status;

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            status = "Authorization header required";
        } else {

            String authScheme = "Bearer ";
            if (!authHeader.startsWith(authScheme)) {
                status = "Authorization scheme error";
            } else {
                String credentials = authHeader.substring(authScheme.length());

                UserAccess userAccess = dataContext.getAccessTokenDao().getUserAccess(credentials);

                if (userAccess == null) {
                    status = "Token expired or not valid";
                } else {
                    status = "Token granted";
                    req.setAttribute("authUserAccess", userAccess);
                }
            }
        }
        req.setAttribute("authStatus", status);
        next.doFilter(request, resp);
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }
}