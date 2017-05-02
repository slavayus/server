package connectDB;


import DataFromClitent.Command;
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
    private Command command;

    private static final String INSERT_PEOPLE_QUERY =
            "INSERT INTO PEOPLE(AGE, NAME) VALUES (?,?);";
    private static final String REMOVE_PEOPLE_QUERY =
            "DELETE FROM PEOPLE WHERE id = ?";
    private static final String UPDATE_PEOPLE_NAME_QUERY =
            "UPDATE PEOPLE SET name = ?;";

    WorkWithDB() {
    }

    public WorkWithDB(ByteArrayOutputStream oldByteData, ByteArrayOutputStream newByteData, Command command) {
        this.oldByteData = oldByteData;
        this.newByteData = newByteData;
        this.command = command;
    }


    public MessageToClient executeCommand() {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            initTable(statement);

            deserializeInputData();

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
        int updateRow = -1;
        switch (command) {
            case UPDATE:
                updateRow = updateData(connection);
                break;
            case REMOVE:
                updateRow = removeData(connection);
                break;
            case INSERT:
                updateRow = insertData(connection);
                break;
        }
        return updateRow;
    }

    private int updateData(Connection connection) {
        int updateRow = -1;
        try {
            connection.setAutoCommit(false);
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_PEOPLE_NAME_QUERY);
            for (Map.Entry<String, Man> entry : newData.entrySet()) {
                updateStatement.setString(1, entry.getValue().getName());
            }
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    private int removeData(Connection connection) {
        int updateRow = -1;
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_PEOPLE_QUERY);
            for (Map.Entry<String, Man> entry : newData.entrySet()) {
                preparedStatement.setInt(1, Integer.parseInt(entry.getKey()));
                updateRow += preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return updateRow;
    }


    private int insertData(Connection connection) {
        int updateRow = -1;
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PEOPLE_QUERY);
            for (Map.Entry<String, Man> entry : newData.entrySet()) {
                preparedStatement.setInt(1, entry.getValue().getAge());
                preparedStatement.setString(2, entry.getValue().getName());
                updateRow += preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return updateRow;
    }

    //true - data in DB and data from client is equals
    private boolean checkOldData(Statement statement) {
        try {
            int size = statement.executeUpdate("SELECT count(*) FROM people;");

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
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(oldByteData.toByteArray()))) {
            family = (Map<String, Man>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(newByteData.toByteArray()))) {
            newData = (Map<String, Man>) objectInputStream.readObject();
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