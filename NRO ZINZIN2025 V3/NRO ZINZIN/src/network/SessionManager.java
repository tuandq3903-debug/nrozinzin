package network;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import network.inetwork.ISession;

public class SessionManager {
    private static volatile SessionManager instance;
    private final List<ISession> sessions = new CopyOnWriteArrayList<>();

    public static SessionManager gI() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) instance = new SessionManager();
            }
        }
        return instance;
    }

    public void putSession(ISession session) {
        sessions.add(session);
    }

    public void removeSession(ISession session) {
        sessions.remove(session);
    }

    public List<ISession> getSessions() {
        return List.copyOf(sessions);
    }

    public void cleanupSessions() {
        Iterator<ISession> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            ISession session = iterator.next();
            if (!session.isConnected()) {
                iterator.remove();
                session.dispose();
            }
        }
    }

    public void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                cleanupSessions();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Session-Cleanup-Thread");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    public ISession findByID(long id) throws Exception {
        for (ISession session : sessions) {
            if (session.getID() == id) return session;
        }
        throw new Exception("Session " + id + " does not exist");
    }

    public int getNumSession() {
        return sessions.size();
    }
}
