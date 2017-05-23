package GUI;

import connectDB.Container;
import old.school.Man;
import org.junit.Test;

import static connectDB.ConnectDB.getConnection;
import static org.junit.Assert.*;

/**
 * Created by slavik on 23.05.17.
 */
public class CRUDTest {
    @Test
    public void initTable() throws Exception {
        ManDAO manDAO = new ManDAO();
        StringBuilder sql = new StringBuilder("CREATE TABLE PEOPLE(\n" +
                "  ID SERIAL PRIMARY KEY,\n" +
                "  AGE INTEGER CONSTRAINT positive_age CHECK (AGE>0) NOT NULL,\n" +
                "  NAME TEXT NOT NULL,\n" +
                "  CREATE_DATE TIMESTAMP\n" +
                ");");
    }

}