package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import network.inetwork.IMessageSendCollect;
import network.inetwork.ISession;
import utils.Logger;

public class MessageSendCollect implements IMessageSendCollect {

    // Con trỏ đọc/ghi key để mã hóa/xor
    private int curR = 0;
    private int curW = 0;

    @Override
    public Message readMessage(ISession session, DataInputStream dis) throws Exception {
        boolean sentKey = session.sentKey();
        byte cmd = dis.readByte();
        if (sentKey) {
            cmd = readKey(session, cmd);
        }

        int size;
        if (sentKey) {
            byte b1 = dis.readByte();
            byte b2 = dis.readByte();
            size = ((readKey(session, b1) & 0xFF) << 8)
                 | (readKey(session, b2) & 0xFF);
        } else {
            size = dis.readUnsignedShort();
        }

        byte[] data = new byte[size];
        int offset = 0, len;
        while (offset < size && (len = dis.read(data, offset, size - offset)) != -1) {
            offset += len;
        }

        if (sentKey) {
            for (int i = 0; i < size; i++) {
                data[i] = readKey(session, data[i]);
            }
        }

        return new Message(cmd, data);
    }

    @Override
    public byte readKey(ISession session, byte b) {
        byte[] key = session.getKey();
        byte result = (byte)((key[curR++] & 0xFF) ^ (b & 0xFF));
        if (curR >= key.length) {
            curR = 0;
        }
        return result;
    }

    /**
     * Mã hóa byte bằng XOR giống readKey nhưng dùng con trỏ curW
     */
    @Override
    public byte writeKey(ISession session, byte b) {
        byte[] key = session.getKey();
        byte result = (byte)((key[curW++] & 0xFF) ^ (b & 0xFF));
        if (curW >= key.length) {
            curW = 0;
        }
        return result;
    }

    @Override
    public void doSendMessage(ISession session, DataOutputStream dos, Message msg) throws Exception {
        try {
            byte[] data = msg.getData();
            boolean sentKey = session.sentKey();
            byte cmd = msg.command;

            // Ghi byte lệnh (có mã hóa nếu bật key)
            dos.writeByte(sentKey ? writeKey(session, cmd) : cmd);

            if (data != null) {
                int size = data.length;

                // Ghi header độ dài
                if (cmd == -32 || cmd == -66 || cmd == -74 || cmd == -28 || cmd == 11 ||
                    cmd == -67 || cmd == -87 || cmd == 66 || cmd == 12) {

                    dos.writeByte(writeKey(session, (byte) size)   - 128);
                    dos.writeByte(writeKey(session, (byte)(size >> 8))   - 128);
                    dos.writeByte(writeKey(session, (byte)(size >> 16))  - 128);

                } else if (sentKey) {
                    dos.writeByte(writeKey(session, (byte)(size >> 8)));
                    dos.writeByte(writeKey(session, (byte)(size & 0xFF)));

                } else {
                    dos.writeShort(size);
                }

                // Mã hóa payload nếu cần
                if (sentKey) {
                    for (int i = 0; i < size; i++) {
                        data[i] = writeKey(session, data[i]);
                    }
                }
                dos.write(data);
            } else {
                // Không có payload, ghi độ dài 0
                dos.writeShort(0);
            }

            dos.flush();
            msg.cleanup();

        } catch (AsynchronousCloseException ace) {
            // Kênh bị đóng đồng thời — chỉ log debug
            Logger.error("Kênh đóng khi gửi, bỏ qua: " + ace);
        } catch (IOException ex) {
            // Lỗi I/O thực sự
            Logger.error("Gửi tin nhắn thất bại: " + ex);
        }
    }

    // Không dùng @Override vì interface không khai báo writeMessage()
    public void writeMessage(ISession session, DataOutputStream dos, Message msg) {
        try {
            doSendMessage(session, dos, msg);
        } catch (Exception e) {
            Logger.error("Ngoại lệ bất ngờ trong writeMessage: " + e);
        }
    }
}
