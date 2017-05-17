package support;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrey on 10.05.17.
 */
//пока что не используется!
@Service
public class TimeOut {
    private Map<String, Date> lastVisit = new ConcurrentHashMap<>();

    public void setVisit(ArrayList<String> logins) {
        logins.forEach(this::setVisit);
    }

    public void setVisit(String login) {
        lastVisit.put(login, new Date());
    }

    public Date getVisit(String login) {
        return lastVisit.get(login);
    }

    public boolean checkAndSet(String login) {
        final Date visit = getVisit(login);
        if (getVisit(login) == null) {
            setVisit(login);
            return true;
        }
        final Date date = new Date();
        if ((date.getTime() - visit.getTime() <= 31000)) {
            setVisit(login);
            return true;
        }
        lastVisit.remove(login);
        return false;
    }
}
