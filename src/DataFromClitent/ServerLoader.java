package DataFromClitent;

import GUI.Button;
import connectDB.MessageToClient;
import connectDB.WorkWithDB;
import old.school.People;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by slavik on 01.05.17.
 */
public class ServerLoader {
    private static ByteArrayOutputStream oldData = new ByteArrayOutputStream();
    private static ByteArrayOutputStream newData = new ByteArrayOutputStream();
    private static Button button;
    private static Data typeOfData = Data.OLD;

    public static void main(String[] args) throws UnknownHostException {
        SocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 7007);
        while (true) {
            try (DatagramChannel serverSocket = DatagramChannel.open().bind(inetSocketAddress)) {
                System.out.println(serverSocket);
                ByteBuffer dataFromClient = ByteBuffer.allocate(8 * 1024);
                while (true) {
                    SocketAddress socketAddress = serverSocket.receive(dataFromClient);

                    String msgFromClient = new String(dataFromClient.array(), 0, dataFromClient.position());


                    MessageToClient messageToClient = analysisMsgFromClient(msgFromClient, dataFromClient);
                    if (messageToClient != null) {
                        messageToClient.sendData(serverSocket, socketAddress);
                    }

                    dataFromClient.clear();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    oldData, NEW, newData, BUTTON, BUTTON, END
     */

    private static MessageToClient analysisMsgFromClient(String msgFromClient, ByteBuffer dataFromClient) throws IOException {

        if (msgFromClient.equals("END")) {
            typeOfData = Data.OLD;
            WorkWithDB workWithDB = new WorkWithDB(oldData, newData, button);
            MessageToClient messageToClient =workWithDB.executeCommand();
            newData.reset();
            oldData.reset();
            return messageToClient;
        }

        try {
            switch (typeOfData) {
                case NEW:
                    newData.write(dataFromClient.array());
                    break;
                case OLD:
                    oldData.write(dataFromClient.array());
                    break;
                case BUTTON:
                    button = Button.valueOf(msgFromClient);
                    break;
            }
        } catch (IllegalArgumentException e) {
            //do nothing
        }

        try {
            typeOfData = Data.valueOf(msgFromClient);
        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
        }

        return null;
    }
}
