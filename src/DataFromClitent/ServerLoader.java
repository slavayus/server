package DataFromClitent;

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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by slavik on 01.05.17.
 */
public class ServerLoader {
    private Map<String,People> family = new ConcurrentHashMap<>();
    private static ByteArrayOutputStream oldData = new ByteArrayOutputStream();
    private static ByteArrayOutputStream newData = new ByteArrayOutputStream();
    private static Command command;
    private static Data typeOfData = Data.OLD;

    public static void main(String[] args) throws UnknownHostException {
        SocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 7007);
        while (true) {
            try (DatagramChannel serverSocket = DatagramChannel.open().bind(inetSocketAddress)) {
                System.out.println(serverSocket);

                ByteBuffer dataFromClient = ByteBuffer.allocate(8*1024);
                while (true) {
                    SocketAddress socketAddress = serverSocket.receive(dataFromClient);

                    String msgFromClient = new String(dataFromClient.array(), 0, dataFromClient.position());


                    MessageToClient messageToClient = analysisMsgFromClient(msgFromClient,dataFromClient);
                    if(messageToClient!=null){
                        messageToClient.sendData(serverSocket, socketAddress);
                    }

//                    ByteBuffer dataToClient = ByteBuffer.wrap(("Echo: " + msgFromClient).getBytes());
//                    serverSocket.send(dataToClient, socketAddress);
//                    System.out.println(new String(dataToClient.array()));

                    dataFromClient.clear();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static MessageToClient analysisMsgFromClient(String msgFromClient, ByteBuffer dataFromClient) throws IOException {
        if(msgFromClient.equals("old")){
            typeOfData = Data.OLD;
        }

        if (typeOfData == Data.NEW){
            newData.write(dataFromClient.array());
        }

        if (typeOfData == Data.OLD){
            oldData.write(dataFromClient.array());
        }

        if (msgFromClient.equals("command")){
            command = Command.valueOf(msgFromClient);
        }

        System.out.println(msgFromClient);

        if (msgFromClient.equals("end")) {
            typeOfData = Data.NEW;
            WorkWithDB workWithDB = new WorkWithDB(oldData,newData,command);
            return workWithDB.executeCommand();
        }

        return null;
    }
}
