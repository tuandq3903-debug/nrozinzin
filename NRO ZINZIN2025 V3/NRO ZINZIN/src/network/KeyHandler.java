package network;


import java.io.DataOutputStream;
import network.Message;
import network.inetwork.IKeySessionHandler;
import network.inetwork.ISession;

public class KeyHandler implements IKeySessionHandler {
    @Override
    public void sendKey(ISession session) {
        Message msg = new Message(-27);
        try {
            byte[] KEYS = session.getKey();
            if (KEYS == null || KEYS.length == 0) {
                throw new IllegalArgumentException("Invalid session key");
            }
            DataOutputStream dos = msg.writer();
            dos.writeByte(KEYS.length);
            dos.writeByte(KEYS[0]);
            for (int i = 1; i < KEYS.length; i++) {
                dos.writeByte(KEYS[i] ^ KEYS[i - 1]);
            }
            session.doSendMessage(msg);
            session.setSentKey(true);
        } catch (Exception e) {
//             e.printStackTrace(); 
        } finally {
            msg.cleanup();
        }
    }
}