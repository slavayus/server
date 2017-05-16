package connectDB;

import DataFromClitent.ServerLoader;
import old.school.Man;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;

/**
 * Created byteArrayOutputStream slavik on 02.05.17.
 */
public class MessageToClient {
    private boolean clientCollectionState;
    private int modifiedRow;
    private Map<String, Man> dataToClient;
    private ByteBuffer outputData;
    private String msgToClient;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ObjectOutputStream objectOutputStream;
    private DatagramChannel serverSocket = ServerLoader.serverSocket;

    MessageToClient(boolean clientCollectionState, int modifiedRow, Map<String, Man> dataToClient, String msgToClient, DatagramChannel serverSocket) {
        this.clientCollectionState = clientCollectionState;
        this.modifiedRow = modifiedRow;
        this.dataToClient = dataToClient;
        this.msgToClient = msgToClient;
        this.serverSocket = serverSocket;
    }

    //newData, NEW, modifiedRow, STATE, clientConnectionState, MSG, msgToClient, END
    public synchronized void sendData(SocketAddress socketAddress) {
        try  {
            System.out.println("YEE");
            Thread.sleep(500);
            sendMap(this.serverSocket, socketAddress);
            sendServiceInformation(this.serverSocket, socketAddress, "NEW");
            sendModifiedRow(this.serverSocket, socketAddress);
            sendServiceInformation(this.serverSocket, socketAddress, "STATE");
            sendClientCollectionState(this.serverSocket, socketAddress);
            sendServiceInformation(this.serverSocket, socketAddress, "MSG");
            sendMsgToClient(this.serverSocket,socketAddress);
            sendServiceInformation(this.serverSocket, socketAddress, "END");
            System.out.println("send" + this.dataToClient + socketAddress+ this.serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMsgToClient(DatagramChannel serverSocket, SocketAddress socketAddress) throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(msgToClient);
        objectOutputStream.flush();
        outputData = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        serverSocket.send(outputData,socketAddress);
    }

    private void sendServiceInformation(DatagramChannel serverSocket, SocketAddress socketAddress, String serviceInformation) throws IOException {
        outputData = ByteBuffer.wrap(serviceInformation.getBytes());
        serverSocket.send(outputData, socketAddress);
    }

    //client data state
    private void sendClientCollectionState(DatagramChannel serverSocket, SocketAddress socketAddress) throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeBoolean(clientCollectionState);
        objectOutputStream.flush();
        outputData = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        serverSocket.send(outputData, socketAddress);
    }

    //how much was modified row
    private void sendModifiedRow(DatagramChannel serverSocket, SocketAddress socketAddress) throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeInt(modifiedRow);
        objectOutputStream.flush();
        outputData = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        serverSocket.send(outputData, socketAddress);
    }

    //new data
    private void sendMap(DatagramChannel serverSocket, SocketAddress socketAddress) throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();
        this.objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        this.objectOutputStream.writeObject(dataToClient);
        objectOutputStream.flush();
        outputData = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        serverSocket.send(outputData, socketAddress);
    }

    void sendNewDataAllClients(SocketAddress socket) {
        try {
            sendMap(serverSocket, socket);
            sendServiceInformation(serverSocket, socket, "END");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
