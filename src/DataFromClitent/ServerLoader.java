package DataFromClitent;

import GUI.Button;
import connectDB.Container;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by slavik on 01.05.17.
 */
public class ServerLoader {
    public static Map<String, Container> containerElement = new HashMap<>();
    private static Container container;
    public static DatagramChannel serverSocket;

    public static void main(String[] args) throws UnknownHostException {
        SocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 7007);
        while (true) {
            try {
                serverSocket = DatagramChannel.open().bind(inetSocketAddress);
                System.out.println(serverSocket);
                ByteBuffer dataFromClient = ByteBuffer.allocate(8 * 1024);
                while (true) {
                    SocketAddress socketAddress = serverSocket.receive(dataFromClient);

                    container = containerElement.computeIfAbsent(socketAddress.toString(), k -> {
                        Container newContainer = new Container(serverSocket, socketAddress);
                        new Thread(newContainer).start();
                        return newContainer;
                    });

                    synchronized (container) {
//                        System.out.println(socketAddress.toString());
                        String msgFromClient = new String(dataFromClient.array(), 0, dataFromClient.position());


                        analysisMsgFromClient(msgFromClient, dataFromClient, serverSocket, socketAddress);

                        dataFromClient.clear();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    oldData, NEW, newData, BUTTON, BUTTON, END
     */

    private static void analysisMsgFromClient(String msgFromClient, ByteBuffer dataFromClient, DatagramChannel serverSocket, SocketAddress socketAddress) throws IOException {
        if (msgFromClient.equals("END")) {
            container.setTypeOfData(Data.OLD);
            return;
        }

        try {
            container.setTypeOfData(Data.valueOf(msgFromClient));
            return;
        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
        }

        try {
            switch (container.getTypeOfData()) {
                case NEW:
                    container.setNewByteData(dataFromClient.array());
                    break;
                case OLD:
                    container.setOldByteData(dataFromClient.array());
                    break;
                case BUTTON:
                    container.setButton(Button.valueOf(msgFromClient));
                    break;
            }
        } catch (IllegalArgumentException e) {
            //do nothing
        }
    }
}
