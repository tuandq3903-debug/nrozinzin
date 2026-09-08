package network;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import javax.imageio.ImageIO;
import network.inetwork.IMessage;

public class Message implements IMessage {
    public byte command;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    private DataInputStream dis;

    public Message(int command) {
        this((byte) command);
    }

    public Message(byte command) {
        this.command = command;
        this.os = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(os);
    }

    public Message(byte command, byte[] data) {
        this.command = command;
        this.is = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(is);
    }

    @Override
    public DataOutputStream writer() {
        return dos;
    }

    @Override
    public DataInputStream reader() {
        return dis;
    }

    @Override
    public byte[] getData() {
        return os != null
                ? os.toByteArray()
                : new byte[0];
    }

    @Override
    public void cleanup() {
        try {
            closeStream(is);
            closeStream(os);
            closeStream(dis);
            closeStream(dos);
        } catch (IOException ignored) {
        }
    }

    private void closeStream(Closeable stream) throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    @Override
    public void dispose() {
        cleanup();
        is = null;
        os = null;
        dis = null;
        dos = null;
    }
    
    @Override
    public int read() throws IOException {
        return reader().read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return reader().read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return reader().read(b, off, len);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return reader().readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return reader().readByte();
    }

    @Override
    public short readShort() throws IOException {
        return reader().readShort();
    }

    @Override
    public int readInt() throws IOException {
        return reader().readInt();
    }

    @Override
    public long readLong() throws IOException {
        return reader().readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return reader().readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return reader().readDouble();
    }

    @Override
    public char readChar() throws IOException {
        return reader().readChar();
    }

    @Override
    public String readUTF() throws IOException {
        return reader().readUTF();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        reader().readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        reader().readFully(b, off, len);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return reader().readUnsignedByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return reader().readUnsignedShort();
    }
    
    @Override
    public void write(byte[] b) throws IOException {
        writer().write(b);
    }

    @Override
    public void write(int b) throws IOException {
        writer().write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writer().write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        writer().writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        writer().writeByte(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        writer().writeBytes(s);
    }

    @Override
    public void writeChar(int v) throws IOException {
        writer().writeChar(v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        writer().writeChars(s);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writer().writeDouble(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writer().writeFloat(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        writer().writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        writer().writeLong(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        writer().writeShort(v);
    }
    

    @Override
    public void writeUTF(String str) throws IOException {
        writer().writeUTF(str);
    }

    // Phương thức đọc ảnh
    @Override
    public BufferedImage readImage() throws IOException {
        int size = readInt();
        byte[] dataImage = new byte[size];
        readFully(dataImage);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(dataImage)) {
            return ImageIO.read(bais);
        }
    }
    @Override
    public void writeImage(BufferedImage image, String format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write((RenderedImage) image, format, baos);
            byte[] dataImage = baos.toByteArray();

            writeInt(dataImage.length);
            write(dataImage);
        }
    }
}
