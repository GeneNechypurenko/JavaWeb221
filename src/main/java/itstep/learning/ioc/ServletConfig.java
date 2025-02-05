package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.servlets.HomeServlet;
import itstep.learning.servlets.TimeServlet;

public class ServletConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/home").with(HomeServlet.class);
        serve("/time").with(TimeServlet.class);
    }
}
