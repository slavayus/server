package GUI;

import connectDB.Container;
import old.school.Man;
import old.school.People;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static connectDB.ConnectDB.getConnection;
import static org.junit.Assert.*;

/**
 * Created by slavik on 21.05.17.
 */
public class ButtonTest {
    @Test
    public void execute() throws Exception {
        People people = new People(255, "kdj");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Skopje"));
        Timestamp timestamp = new Timestamp(zonedDateTime.toInstant().getEpochSecond() * 1000L);

        people.setTime(zonedDateTime);


        people.setId("3");
        Map<String,Man> manMap = new LinkedHashMap<>();
        manMap.put("12",people);
        Button.LOAD.execute(manMap);

    }


    @Test
    public void insertNewRowQuery() throws Exception {
        Map<String, Man> newData = new HashMap<>();
        People people = new People(23, "knd");
        newData.put("32253", people);
//        Button.INSERT_NEW_OBJECT.insertNewRowQuery(getConnection(), newData);

        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("mk", "MK"));


        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Skopje"));


        String formattedDate = f.format(new Timestamp(zonedDateTime.toInstant().getEpochSecond() * 1000L));
        System.out.println("Date: " + formattedDate);
        Timestamp timestamp = new Timestamp(zonedDateTime.toInstant().getEpochSecond() * 1000L);
        ZonedDateTime zonedDateTime1 = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
        System.out.println(zonedDateTime1);

        //ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Moscow"));
        //ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Sofia"));
        //ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Pacific/Auckland"));
//        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Skopje"));
//        preparedStatement.setTimestamp(4, new Timestamp(zonedDateTime.toInstant().getEpochSecond()*1000L));

    }

}