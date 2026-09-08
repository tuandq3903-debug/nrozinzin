package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import network.inetwork.IMessageSendCollect;
import network.inetwork.ISession;

public final class Sender implements Runnable {

    private ISession session;
    private BlockingDeque<Message> messages = new LinkedBlockingDeque<>();
    private DataOutputStream dos;
    private IMessageSendCollect sendCollect;

    public Sender(@NonNull ISession session, @NonNull Socket socket) {
        this.session = session;
        setSocket(socket);
    }

    public Sender setSocket(@NonNull Socket socket) {
        try {
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ignored) {
        }
        return this;
    }

    @Override
    public void run() {
        try {
            while (session.isConnected()) {
                Message message = messages.poll(5, TimeUnit.SECONDS);
                if (message != null) {
                    doSendMessage(message);
                    message.cleanup();
                }
                TimeUnit.MILLISECONDS.sleep(10);
            }
        } catch (Exception ignored) {
        }
    }

    public synchronized void doSendMessage(Message message) throws Exception {
        sendCollect.doSendMessage(session, dos, message);
    }

    public void sendMessage(Message msg) {
        if (session.isConnected()) {
            messages.add(msg);
        }
    }

    public void setSend(IMessageSendCollect sendCollect) {
        this.sendCollect = sendCollect;
    }

    public int getNumMessages() {
        return messages.size();
    }

    public void close() {
        messages.clear();
        try {
            if (dos != null) dos.close();
        } catch (IOException ignored) {
        }
    }

    public void dispose() {
        session = null;
        messages = null;
        sendCollect = null;
        dos = null;
    }
}