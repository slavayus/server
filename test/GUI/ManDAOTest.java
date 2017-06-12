package GUI;

import old.school.Man;
import old.school.People;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Created by slavik on 24.05.17.
 */
public class ManDAOTest {
    @Test
    public void list() throws Exception {
        ManDAO manDAO = new ManDAO();
        Map<String, Man> list = manDAO.list();
        list.values()
                .forEach(System.out::println);


    }

    @Test
    public void selectRow() throws Exception {
        ManDAO manDAO = new ManDAO();
//        System.out.println(manDAO.selectRow("ORDER BY AGE LIMIT 1").getName());
    }

    @Test
    public void update() {
        ManDAO manDAO = new ManDAO();
        People people = new People(23, "kdj");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Skopje"));
        Timestamp timestamp = new Timestamp(zonedDateTime.toInstant().getEpochSecond() * 1000L);

        people.setTime(zonedDateTime);


        people.setId("3");
        manDAO.update(people);
    }


    @Test
    public void insert() {
        String s = new String();
        System.out.println(s.getClass());

        ManDAO manDAO = new ManDAO();
        People people = new People(23, "kdj");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Skopje"));
        Timestamp timestamp = new Timestamp(zonedDateTime.toInstant().getEpochSecond() * 1000L);

        people.setTime(zonedDateTime);


        people.setId("3");
        manDAO.insert(people, false);
    }


    @Test
    public void remove(){
        ManDAO manDAO = new ManDAO();
        People people = new People(23, "kdj");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Skopje"));
        Timestamp timestamp = new Timestamp(zonedDateTime.toInstant().getEpochSecond() * 1000L);

        people.setTime(zonedDateTime);


        people.setId("3");
        manDAO.remove(people);
    }


}