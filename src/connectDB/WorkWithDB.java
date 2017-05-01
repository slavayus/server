package connectDB;


import DataFromClitent.Command;
import old.school.Man;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.io.*;
import java.sql.*;
import java.util.Iterator;
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

    WorkWithDB() {
    }

    public WorkWithDB(ByteArrayOutputStream oldByteData, ByteArrayOutputStream newByteData, Command command) {
        this.oldByteData = oldByteData;
        this.newByteData = newByteData;
        this.command = command;
    }


    public void executeCommand() {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            initTable(statement);

            deserializeInputData();

            //true - data in DB and data from client is equals
            boolean state = checkOldData(statement);

            int modifiedRow = modifyDataInDB(connection);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private int modifyDataInDB(Connection connection) {
        int updateRow = -1;
        switch (command) {
            case UPDATE:
//                updateRow = updateData();
                break;
            case REMOVE:
//                updateRow = removeData();
                break;
            case INSERT:
                updateRow = insertData(connection);
                break;
        }
        return updateRow;
    }


    private int insertData(Connection connection) {
        int updateRow = -1;
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PEOPLE_QUERY);
            for (Map.Entry<String, Man> entry : newData.entrySet()) {
                preparedStatement.setInt(1,entry.getValue().getAge());
                preparedStatement.setString(2, entry.getValue().getName());
                updateRow += preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return updateRow;
    }


    private boolean checkOldData(Statement statement) {
        try {
            int size = statement.executeUpdate("SELECT count(*) FROM people_id_seq;");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM people;");

            if (size != family.size()) {
                return false;
            }

            Iterator<Map.Entry<String, Man>> iterator = family.entrySet().iterator();
            while (resultSet.next()) {
                Map.Entry<String, Man> entry = iterator.next();
                if (!resultSet.getString(1).equals(entry.getKey())) {
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