package connectDB;

import com.sun.istack.internal.NotNull;
import org.postgresql.ds.PGConnectionPoolDataSource;

import javax.sql.PooledConnection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by slavik on 23.05.17.
 */
public class ConnectDB {
    private static final String FILE_NAME_DB_PROPERTIES = "DataBase.properties";

    @NotNull
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
        try (InputStream scanner = ConnectDB.class.getResourceAsStream("/properties/" + FILE_NAME_DB_PROPERTIES)) {
            platformProperties.load(scanner);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not connect");
        }


        Properties dataBaseProperties = new Properties();
        try (InputStream scanner = ConnectDB.class.getResourceAsStream("/properties/" + platformProperties.getProperty("platform"))) {
            dataBaseProperties.load(scanner);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not connect");
        }

        return dataBaseProperties;
    }
}
