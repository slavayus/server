package connectDB;


import GUI.Button;
import old.school.Man;
import old.school.People;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.io.*;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by slavik on 30.04.17.
 */
public class WorkWithDB {

    private static final String FILE_NAME_DB_PROPERTIES = "DataBase.properties";
    private ByteArrayOutputStream oldByteData;
    private ByteArrayOutputStream newByteData;
    private Map<String, Man> family;
    private Map<String, Man> newData;
    private Button button;



    WorkWithDB() {
    }

    public WorkWithDB(ByteArrayOutputStream oldByteData, ByteArrayOutputStream newByteData, Button button) {
        this.oldByteData = oldByteData;
        this.newByteData = newByteData;
        this.button = button;
    }


    public MessageToClient executeCommand() {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            initTable(statement);

            deserializeInputData();

            connection.close();
            return new MessageToClient(checkOldData(statement), modifyDataInDB(connection), getNewDataForClient(statement));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Map<String, Man> getNewDataForClient(Statement statement) {
        Map<String, Man> dataFromDB = new LinkedHashMap<>();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM people");
            while (resultSet.next()) {
                dataFromDB.put((String.valueOf(resultSet.getInt(1))), new People(resultSet.getInt(2), resultSet.getString(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dataFromDB;
    }

    private int modifyDataInDB(Connection connection) {
        return  button.execute(connection,family,newData);
    }


    //true - data in DB and data from client is equals
    private boolean checkOldData(Statement statement) {
        try {
            ResultSet sizeResultSet = statement.executeQuery("SELECT count(*) FROM people;");
            sizeResultSet.next();
            int size = sizeResultSet.getInt(1);

            ResultSet resultSet = statement.executeQuery("SELECT * FROM people;");

            if (size != family.size()) {
                return false;
            }


            while (resultSet.next()) {
                if (!family.containsKey(resultSet.getString(1))) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    private void initTable(Statement statement) {
        String createTable = "CREATE TABLE PEOPLE(\n" +
                "  ID SERIAL PRIMARY KEY,\n" +
                "  AGE INTEGER CONSTRAINT positive_age CHECK (AGE>0) NOT NULL,\n" +
                "  NAME TEXT \n" +
                ");";
        try {
            statement.executeUpdate(createTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deserializeInputData() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(newByteData.toByteArray());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            newData = (Map<String, Man>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(oldByteData.toByteArray()))) {
            family = (Map<String, Man>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    private Connection getConnection() throws SQLException {
        Properties dataBaseProperties = getProperties();
        PGConnectionPoolDataSource pgConnectionPoolDataSource = new PGConnectionPoolDataSource();
        pgConnectionPoolDataSource.setDatabaseName(dataBaseProperties.getProperty("jdbs.dbname"));
        pgConnectionPoolDataSource.setServerName(dataBaseProperties.getProperty("jdbs.servername"));
        PooledConnection pooledConnection = pgConnectionPoolDataSource.getPooledConnection(dataBaseProperties.getProperty("jdbs.username"), dataBaseProperties.getProperty("jdbs.password"));

        return pooledConnection.getConnection();
    }

    private Properties getProperties() {

        Properties platformProperties = new Properties();
        try (InputStream scanner = WorkWithDB.class.getResourceAsStream("/properties/" + FILE_NAME_DB_PROPERTIES)) {
            platformProperties.load(scanner);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not connect");
        }


        Properties dataBaseProperties = new Properties();
        try (InputStream scanner = WorkWithDB.class.getResourceAsStream("/properties/" + platformProperties.getProperty("platform"))) {
            dataBaseProperties.load(scanner);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not connect");
        }

        return dataBaseProperties;
    }

}