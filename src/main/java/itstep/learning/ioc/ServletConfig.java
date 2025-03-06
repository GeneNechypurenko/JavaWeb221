package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.filters.AuthFilter;
import itstep.learning.filters.AuthJwtFilter;
import itstep.learning.filters.CharsetFilter;
import itstep.learning.servlets.*;

public class ServletConfig extends ServletModule {

    @Override
    protected void configureServlets() {
        filter("/*").through(CharsetFilter.class);
        filter("/*").through(AuthFilter.class);
        // filter("/*").through(AuthJwtFilter.class);
        serve("/home").with(HomeServlet.class);
        serve("/time").with(TimeServlet.class);
        serve("/user").with(UserServlet.class);
        serve("/random").with(RandomServlet.class);
        serve("/product").with(ProductServlet.class);
        serve("/storage/*").with(StorageServlet.class);
    }
}
