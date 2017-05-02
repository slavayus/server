package connectDB;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import old.school.Man;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;

/**
 * Created by slavik on 02.05.17.
 */
public class MessageToClient {
    private boolean clientCollectionState;
    private int modifiedRow;
    private Map<String, Man> dataToClient;
    private ByteBuffer outputData;
    private static final ByteOutputStream byteOutputStream = new ByteOutputStream();

    MessageToClient(boolean clientCollectionState, int modifiedRow, Map<String, Man> dataToClient) {
        this.clientCollectionState = clientCollectionState;
        this.modifiedRow = modifiedRow;
        this.dataToClient = dataToClient;
    }


    public void sendData(DatagramChannel serverSocket, SocketAddress socketAddress) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream)) {

            sendMap(serverSocket, socketAddress, objectOutputStream);
            senModifiedRow(serverSocket,socketAddress,objectOutputStream);
            sendClientCollectionState(serverSocket,socketAddress,objectOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendClientCollectionState(DatagramChannel serverSocket, SocketAddress socketAddress, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeBoolean(clientCollectionState);
        outputData = ByteBuffer.wrap(byteOutputStream.getBytes());
        serverSocket.send(outputData,socketAddress);
    }

    private void senModifiedRow(DatagramChannel serverSocket, SocketAddress socketAddress, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(modifiedRow);
        outputData = ByteBuffer.wrap(byteOutputStream.getBytes());
        serverSocket.send(outputData,socketAddress);
    }

    private void sendMap(DatagramChannel serverSocket, SocketAddress socketAddress, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(dataToClient);
        outputData = ByteBuffer.wrap(byteOutputStream.getBytes());
        serverSocket.send(outputData, socketAddress);

    }
}
