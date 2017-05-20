package connectDB;

import DataFromClitent.ServerLoader;
import old.school.Man;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;

/**
 * Created byteArrayOutputStream slavik on 02.05.17.
 */
public class MessageToClient implements Serializable {
    private boolean clientCollectionState;
    private int modifiedRow;
    private Map<String, Man> dataToClient;
    private String msgToClient;
    private static final long serialVersionUID =2;

    public MessageToClient() {
    }

    MessageToClient(boolean clientCollectionState, int modifiedRow, Map<String, Man> dataToClient, String msgToClient) {
        this.clientCollectionState = clientCollectionState;
        this.modifiedRow = modifiedRow;
        this.dataToClient = dataToClient;
        this.msgToClient = msgToClient;
    }

}
