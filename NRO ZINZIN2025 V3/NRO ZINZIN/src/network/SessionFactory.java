package network;

import java.net.Socket;
import network.inetwork.ISession;

public class SessionFactory {
    private static SessionFactory instance;

    public static SessionFactory gI() {
        if (instance == null) {
            synchronized (SessionFactory.class) {
                if (instance == null) {
                    instance = new SessionFactory();
                }
            }
        }
        return instance;
    }

    public <T extends ISession> T cloneSession(Class<T> clazz, Socket socket) throws Exception {
        return clazz.getConstructor(Socket.class).newInstance(socket);
    }
}