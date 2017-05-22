package connectDB;

import DataFromClitent.Data;
import DataFromClitent.ServerLoader;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    private volatile byte numOfReceive;
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
            Statement statement = connection.createStatement();

            initTable(statement);

            deserializeInputData();

            MessageToClient messageToClient = new MessageToClient(
                    checkOldData(statement),
                    modifyDataInDB(connection),
                    getNewDataForClient(statement),
                    Button.getMsgToClient());
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
            family = (Map<String, Man>) objectInputStream.readObject();
            ((ByteArrayOutputStream) oldByteData).reset();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    private void initTable(Statement statement) {
        String createTable = "CREATE TABLE PEOPLE(\n" +
                "  ID SERIAL PRIMARY KEY,\n" +
                "  AGE INTEGER CONSTRAINT positive_age CHECK (AGE>=0) NOT NULL,\n" +
                "  NAME TEXT, \n" +
                "  CREATE_DATE TIMESTAMP \n"+
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
                People people = new People(resultSet.getInt(2), resultSet.getString(3));
                people.setTime(ZonedDateTime.ofInstant(resultSet.getTimestamp(4).toInstant(), ZoneOffset.UTC));
                dataFromDB.put((String.valueOf(resultSet.getInt(1))), people);
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
        numOfReceive++;
        this.typeOfData = typeOfData;
    }

    public synchronized Data getTypeOfData() {
        return typeOfData;
    }

    public synchronized SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public static Connection getConnection() {
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

    private static Properties getProperties() {

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
}
