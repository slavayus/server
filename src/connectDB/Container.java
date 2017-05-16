package connectDB;

import DataFromClitent.Data;
import GUI.Button;
import old.school.Man;
import old.school.People;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

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
    private byte numOfReceive;
    private static final String FILE_NAME_DB_PROPERTIES = "DataBase.properties";
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
//                System.out.println("8");
                if (numOfReceive == 3) {
                    System.out.println("1");
                    MessageToClient messageToClient = executeCommand();
                    if (messageToClient != null) {
                        messageToClient.sendData(serverSocket, socketAddress);
                    }
                }
            }
        }
    }


    private MessageToClient executeCommand() {
        try {
            numOfReceive = 0;
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            initTable(statement);

            deserializeInputData();

            MessageToClient messageToClient = new MessageToClient(checkOldData(statement), modifyDataInDB(connection), getNewDataForClient(statement), Button.getMsgToClient());
            connection.close();
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
            ((ByteArrayOutputStream) oldByteData).reset();
            family = (Map<String, Man>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    private void initTable(Statement statement) {
        String createTable = "CREATE TABLE PEOPLE(\n" +
                "  ID SERIAL PRIMARY KEY,\n" +
                "  AGE INTEGER CONSTRAINT positive_age CHECK (AGE>=0) NOT NULL,\n" +
                "  NAME TEXT \n" +
                ");";
        try {
            statement.executeUpdate(createTable);
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
        }
    }

    private Map<String, Man> getNewDataForClient(Statement statement) {
        Map<String, Man> dataFromDB = new LinkedHashMap<>();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM people");
            while (resultSet.next()) {
                dataFromDB.put((String.valueOf(resultSet.getInt(1))), new People(resultSet.getInt(2), resultSet.getString(3)));
            }
            resultSet.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dataFromDB;
    }

    private int modifyDataInDB(Connection connection) {
        return button.execute(connection, family, newData);
    }


    //true - data in DB and data from client is equals
    private boolean checkOldData(Statement statement) {
        try {
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

    public OutputStream getOldByteData() {
        return oldByteData;
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
        this.typeOfData = typeOfData;
    }

    public synchronized Data getTypeOfData() {
        return typeOfData;
    }

    public synchronized SocketAddress getSocketAddress() {
        return socketAddress;
    }

    private Connection getConnection() {
        Properties dataBaseProperties = getProperties();
        PGConnectionPoolDataSource pgConnectionPoolDataSource = new PGConnectionPoolDataSource();
        pgConnectionPoolDataSource.setDatabaseName(dataBaseProperties.getProperty("jdbs.dbname"));
        pgConnectionPoolDataSource.setServerName(dataBaseProperties.getProperty("jdbs.servername"));
        PooledConnection pooledConnection = null;
        try {
            pooledConnection = pgConnectionPoolDataSource.getPooledConnection(dataBaseProperties.getProperty("jdbs.username"), dataBaseProperties.getProperty("jdbs.password"));
            return pooledConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Properties getProperties() {

        Properties platformProperties = new Properties();
        try (InputStream scanner = Container.class.getResourceAsStream("/properties/" + FILE_NAME_DB_PROPERTIES)) {
            platformProperties.load(scanner);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not connect");
        }


        Properties dataBaseProperties = new Properties();
        try (InputStream scanner = Container.class.getResourceAsStream("/properties/" + platformProperties.getProperty("platform"))) {
            dataBaseProperties.load(scanner);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not connect");
        }

        return dataBaseProperties;
    }


    public void sendDB(DatagramChannel serverSocket, SocketAddress socketAddress) {
        try {


            Statement statement = getConnection().createStatement();


            Map<String, Man> newData = getNewDataForClient(statement);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(newData);
            objectOutputStream.flush();
            ByteBuffer outputData = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            serverSocket.send(outputData, socketAddress);
            outputData = ByteBuffer.wrap("END".getBytes());
            serverSocket.send(outputData, socketAddress);
            statement.close();
//            connection.close();
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public OutputStream getNewByteData() {
        return newByteData;
    }
}
