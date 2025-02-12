package itstep.learning.ioc;

import com.google.inject.AbstractModule;
import itstep.learning.rest.RestService;
import itstep.learning.services.datetime.CurrentDateTimeService;
import itstep.learning.services.datetime.DateTimeService;
import itstep.learning.services.db.DbService;
import itstep.learning.services.db.MySqlDbService;
import itstep.learning.services.hash.HashService;
import itstep.learning.services.hash.Md5HashService;
import itstep.learning.services.kdf.KdfService;
import itstep.learning.services.kdf.PbKdf1Service;
import itstep.learning.services.random.RandomService;
import itstep.learning.services.random.UtilRandomService;


public class ServiceConfig extends AbstractModule {

    @Override
    protected void configure() {
        bind(RandomService.class).to(UtilRandomService.class);
        bind(HashService.class).to(Md5HashService.class);
        bind(KdfService.class).to(PbKdf1Service.class);
        bind(DbService.class).to(MySqlDbService.class);
        bind(DateTimeService.class).to(CurrentDateTimeService.class);
    }
}
