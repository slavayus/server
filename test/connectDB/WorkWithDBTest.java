package connectDB;

import org.junit.Test;

import java.sql.ResultSet;

/**
 * Created by slavik on 01.05.17.
 */
public class WorkWithDBTest {
    @Test
    public void executeCommand() throws Exception {
        WorkWithDB workWithDB = new WorkWithDB();
        ResultSet resultSet = workWithDB.executeCommand();
        while (resultSet.next()){
            System.out.println(resultSet.getString(1));
            System.out.println(resultSet.getString(2));
            System.out.println(resultSet.getString(3));
        }
    }

}