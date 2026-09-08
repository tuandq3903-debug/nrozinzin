package network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Giữ trạng thái mỗi kết nối: channel + buffer đọc + queue ghi.
 */
public class ConnectionContext {
    public final SocketChannel channel;
    public final ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    public final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();

    public ConnectionContext(SocketChannel channel) {
        this.channel = channel;
    }
}
