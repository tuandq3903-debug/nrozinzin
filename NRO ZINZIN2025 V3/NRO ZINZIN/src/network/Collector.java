package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import network.inetwork.IMessageHandler;
import network.inetwork.IMessageSendCollect;
import network.inetwork.ISession;

public final class Collector implements Runnable {
    private ISession session;
    private DataInputStream dis;
    private IMessageSendCollect collect;
    private IMessageHandler messageHandler;

    public Collector(ISession session, Socket socket) {
        this.session = session;
        setSocket(socket);
    }

    public Collector setSocket(Socket socket) {
        try {
            this.dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ignored) {
        }
        return this;
    }

    @Override
    public void run() {
        try {
            while (session != null && session.isConnected()) {
                Message msg = collect.readMessage(session, dis);
                try {
                    if (msg.command == -27) {
                        session.sendKey();
                    } else {
                        messageHandler.onMessage(session, msg);
                    }
                } finally {
                    msg.cleanup();
                }
            }
        } catch (Exception ignored) {
        } finally {
            try {
                Network.gI().getAcceptHandler().sessionDisconnect(session);
            } catch (Exception ignored) {
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void setCollect(IMessageSendCollect collect) {
        this.collect = collect;
    }

    public void setMessageHandler(IMessageHandler handler) {
        this.messageHandler = handler;
    }

    public void close() {
        if (dis != null) {
            try {
                dis.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void dispose() {
        session = null;
        dis = null;
        collect = null;
        messageHandler = null;
    }
}