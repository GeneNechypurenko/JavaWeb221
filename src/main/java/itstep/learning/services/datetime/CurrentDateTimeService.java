package itstep.learning.services.datetime;

public class CurrentDateTimeService implements DateTimeService {
    @Override
    public long getTimestamp() {
        return System.currentTimeMillis();
    }
}
