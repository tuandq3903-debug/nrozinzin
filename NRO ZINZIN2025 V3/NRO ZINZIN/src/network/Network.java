package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.*;
import java.util.Iterator;
import network.inetwork.INetwork;
import network.inetwork.IServerClose;
import network.inetwork.ISession;
import network.inetwork.ISessionAcceptHandler;
import utils.Logger;

public class Network implements INetwork, Runnable {

    private static Network instance;
    private int port = -1;
    private ServerSocketChannel serverSocketChannel;
    private Class sessionClone = Session.class;
    private boolean start;
    private boolean randomKey;
    private IServerClose serverClose;
    private ISessionAcceptHandler acceptHandler;
    private Thread loopServer;
    private Selector selector;

    public static Network gI() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    private Network() {}

    @Override
    public INetwork init() {
        try {
            selector = Selector.open();
            loopServer = new Thread(this, "Network");
        } catch (IOException ex) {
            Logger.error("Failed to open selector: " + ex);
        }
        return this;
    }

    @Override
    public INetwork start(int port) throws Exception {
        if (port < 0) {
            throw new IllegalArgumentException("Please initialize the server port!");
        }
        if (acceptHandler == null) {
            throw new IllegalStateException("AcceptHandler has not been initialized!");
        }
        if (!ISession.class.isAssignableFrom(sessionClone)) {
            throw new IllegalArgumentException("The session clone type is invalid!");
        }

        try {
            this.port = port;
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            start = true;
            loopServer.start();
            Logger.success("Server initialized and listening on port " + port + "\n");
        } catch (IOException ex) {
            Logger.error("Error initializing server at port " + port + ": " + "\n");
            throw ex;
        }

        return this;
    }

    @Override
    public INetwork close() {
        start = false;
        try {
            if (serverSocketChannel != null) serverSocketChannel.close();
        } catch (IOException ex) {
            Logger.error("Error closing server socket: " + ex);
        }

        if (serverClose != null) {
            serverClose.serverClose();
        }
        return this;
    }

    @Override
    public INetwork dispose() {
        acceptHandler = null;
        loopServer = null;
        serverSocketChannel = null;
        return this;
    }

    @Override
    public INetwork setAcceptHandler(ISessionAcceptHandler handler) {
        this.acceptHandler = handler;
        return this;
    }

    @Override
    public void run() {
        while (start) {
            try {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isAcceptable()) continue;

                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    Socket socket = server.accept().socket();

                    ISession session = SessionFactory.gI().cloneSession(sessionClone, socket);
                    acceptHandler.sessionInit(session);
                    SessionManager.gI().putSession(session);
                }
            } catch (IOException ex) {
                Logger.error("IOException in server loop: " + ex);
            } catch (Exception ex) {
                Logger.error("Exception in server loop: " + ex);
            }
        }
    }

    @Override
    public INetwork setDoSomeThingWhenClose(IServerClose serverClose) {
        this.serverClose = serverClose;
        return this;
    }

    @Override
    public INetwork randomKey(boolean isRandom) {
        this.randomKey = isRandom;
        return this;
    }

    @Override
    public boolean isRandomKey() {
        return randomKey;
    }

    @Override
    public INetwork setTypeSessioClone(Class clazz) throws Exception {
        this.sessionClone = clazz;
        return this;
    }

    @Override
    public ISessionAcceptHandler getAcceptHandler() throws Exception {
        if (acceptHandler == null) {
            throw new IllegalStateException("AcceptHandler has not been initialized!");
        }
        return acceptHandler;
    }

    @Override
    public void stopConnect() {
        start = false;
    }
}