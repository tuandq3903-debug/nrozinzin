package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import network.inetwork.IKeySessionHandler;
import network.inetwork.IMessageHandler;
import network.inetwork.IMessageSendCollect;
import network.inetwork.ISession;
import utils.StringUtil;

public class Session implements ISession {
    private static ISession instance;
    private static int ID_INIT;
    private byte[] KEYS = "ZINZIN".getBytes();
    private boolean sentKey;
    public final int id = ID_INIT++;
    private Socket socket;
    private boolean connected;
    private Sender sender;
    private Collector collector;
    private final Thread tSender;
    private final Thread tCollector;
    private IKeySessionHandler keyHandler;
    private final String ip;

    public static ISession gI() throws Exception {
        if (instance == null) throw new Exception("Instance has not been initialized!");
        return instance;
    }

    public Session(Socket socket) {
        this.socket = socket;
        try {
            socket.setSendBufferSize(0x100000);
            socket.setReceiveBufferSize(0x100000);
        } catch (SocketException ignored) {}

        this.connected = true;
        this.ip = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().toString().replace("/", "");
        this.sender = new Sender(this, socket);
        this.collector = new Collector(this, socket);
        this.tSender = new Thread(sender, "Sender - IP : " + ip);
        this.tCollector = new Thread(collector, "Collector - IP : " + ip);
    }

    @Override
    public void sendMessage(Message msg) {
        if (connected && msg != null) sender.sendMessage(msg);
    }

    @Override
    public ISession setSendCollect(IMessageSendCollect collect) {
        sender.setSend(collect);
        collector.setCollect(collect);
        return this;
    }

    @Override
    public ISession setMessageHandler(IMessageHandler handler) {
        collector.setMessageHandler(handler);
        return this;
    }

    @Override
    public ISession setKeyHandler(IKeySessionHandler handler) {
        this.keyHandler = handler;
        return this;
    }

    @Override
    public ISession startSend() {
        if (!tSender.isAlive()) tSender.start();
        return this;
    }

    @Override
    public ISession startCollect() {
        if (!tCollector.isAlive()) tCollector.start();
        return this;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public void disconnect() {
        connected = false;
        sentKey = false;
        if (sender != null) sender.close();
        if (collector != null) collector.close();
        if (socket != null) {
            try { socket.close(); } catch (IOException ignored) {}
        }
        dispose();
    }

    @Override
    public void dispose() {
        if (sender != null) sender.dispose();
        if (collector != null) collector.dispose();
        socket = null;
        sender = null;
        collector = null;
        SessionManager.gI().removeSession(this);
    }

    @Override
    public void sendKey() throws Exception {
        if (keyHandler == null) throw new Exception("Key handler has not been initialized!");
        if (Network.gI().isRandomKey()) KEYS = StringUtil.randomText(7).getBytes();
        keyHandler.sendKey(this);
    }

    @Override
    public boolean sentKey() {
        return sentKey;
    }

    @Override
    public void setSentKey(boolean sent) {
        sentKey = sent;
    }

    @Override
    public void doSendMessage(Message msg) throws Exception {
        sender.doSendMessage(msg);
    }

    @Override
    public ISession start() {
        startSend();
        startCollect();
        return this;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public byte[] getKey() {
        return KEYS;
    }

    @Override
    public int getNumMessages() {
        return isConnected() ? sender.getNumMessages() : -1;
    }
}