package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.filters.CharsetFilter;
import itstep.learning.servlets.*;

public class ServletConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        filter("/*").through(CharsetFilter.class);
        serve("/home").with(HomeServlet.class);
        serve("/time").with(TimeServlet.class);
        serve("/user").with(UserServlet.class);
        serve("/random").with(RandomServlet.class);
    }
}
