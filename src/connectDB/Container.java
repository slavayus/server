package connectDB;

import DataFromClitent.Data;
import GUI.Button;
import GUI.ManDAO;
import old.school.Man;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static connectDB.ConnectDB.getConnection;

/**
 * Created by slavik on 15.05.17.
 */
public class Container implements Runnable {
    private OutputStream oldByteData = new ByteArrayOutputStream();
    private OutputStream newByteData = new ByteArrayOutputStream();
    private Map<String, Man> family;
    private Map<String, Man> newData;
    private Button button;
    private Data typeOfData = Data.OLD;
    private volatile byte numOfReceive;
    private DatagramChannel serverSocket;
    private SocketAddress socketAddress;


    public Container(DatagramChannel serverSocket, SocketAddress socketAddress) {
        this.serverSocket = serverSocket;
        this.socketAddress = socketAddress;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (numOfReceive == 6) {
                    MessageToClient messageToClient = executeCommand();
                    if (messageToClient != null) {
                        try {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(messageToClient);
                            objectOutputStream.flush();
                            serverSocket.send(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()), socketAddress);
                            System.out.println
                                    (socketAddress);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }


    private MessageToClient executeCommand() {
        try {
            numOfReceive = 0;
            Connection connection = getConnection();
            ManDAO manDAO = new ManDAO();
            manDAO.initTable(connection, Man.class);

            deserializeInputData();

            MessageToClient messageToClient = new MessageToClient(
                    checkOldData(connection),
                    modifyDataInDB(connection),
                    manDAO.list(),
                    Button.getMsgToClient());

            if (connection != null) {
                connection.close();
            }
            return messageToClient;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    private void deserializeInputData() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(((ByteArrayOutputStream) newByteData).toByteArray());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            newData = (Map<String, Man>) objectInputStream.readObject();
            ((ByteArrayOutputStream) newByteData).reset();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(((ByteArrayOutputStream) oldByteData).toByteArray()))) {
            family = (Map<String, Man>) objectInputStream.readObject();
            ((ByteArrayOutputStream) oldByteData).reset();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    private int modifyDataInDB(Connection connection) {
        return button.execute(newData);
    }


    //true - data in DB and data from client is equals
    private boolean checkOldData(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet sizeResultSet = statement.executeQuery("SELECT count(*) FROM people;");
            sizeResultSet.next();
            int size = sizeResultSet.getInt(1);
            sizeResultSet.close();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM people;");

            if (size != family.size()) {
                return false;
            }

            while (resultSet.next()) {
                if (!(family.containsKey(resultSet.getString(1)) &&
                        family.get(resultSet.getString(1)).getName().equals(resultSet.getString(3)) &&
                        family.get(resultSet.getString(1)).getAge() == resultSet.getInt(2))) {
                    return false;
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }


    public synchronized void setOldByteData(byte[] oldByteData) throws IOException {
        this.oldByteData.write(oldByteData);
        numOfReceive++;
    }

    public synchronized void setNewByteData(byte[] newByteData) throws IOException {
        this.newByteData.write(newByteData);
        ++numOfReceive;
    }


    public synchronized void setButton(Button button) {

        this.button = button;
        ++numOfReceive;
    }

    public synchronized void setTypeOfData(Data typeOfData) {
        numOfReceive++;
        this.typeOfData = typeOfData;
    }

    public synchronized Data getTypeOfData() {
        return typeOfData;
    }
}
