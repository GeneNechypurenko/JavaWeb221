package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.servlets.HomeServlet;

public class ServletConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/home").with(HomeServlet.class);
    }
}
