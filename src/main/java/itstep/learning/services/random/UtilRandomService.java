package itstep.learning.services.random;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.datetime.DateTimeService;
import java.util.Random;

@Singleton
public class UtilRandomService implements RandomService {
    private final Random rand;

    @Inject
    public UtilRandomService(DateTimeService dateTimeService) {
        this.rand = new Random(dateTimeService.getTimestamp());
    }

    @Override
    public int randomInt() {
        return rand.nextInt();
    }
}
