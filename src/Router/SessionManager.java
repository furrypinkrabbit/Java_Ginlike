package Router;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private Map<String, Map<String, Object>> sessions = new HashMap<>();

    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new HashMap<>());
        return sessionId;
    }

    public Map<String, Object> getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void setSessionAttribute(String sessionId, String key, Object value) {
        Map<String, Object> session = sessions.get(sessionId);
        if (session != null) {
            session.put(key, value);
        }
    }
}
