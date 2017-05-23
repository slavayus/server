package GUI;

import old.school.Man;
import old.school.annotation.Column;
import old.school.annotation.Entity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by slavik on 23.05.17.
 */
public interface CRUD {
    Map<String, Man> select(Man man);

    int insert(Map<String, Man> family, boolean withKey);

    int remove(Man man);

    void update(Map<String, Man> newData);

    default void initTable(Connection connection, Class cl) {
        Entity entity = (Entity) cl.getAnnotation(Entity.class);

        List<Column> columns = new LinkedList<>();

        Arrays.stream(cl.getDeclaredFields()).
                forEach(field -> columns.add(field.getAnnotation(Column.class)));

        StringBuilder createTable = new StringBuilder("CREATE TABLE ");
        createTable.append(entity.tableName());
        createTable.append("(\n");

        columns.stream().
                filter(Objects::nonNull).
                forEach(column -> {
                    createTable.append("  ");
                    createTable.append(column.name());
                    createTable.append(" ");
                    createTable.append(column.type());
                    createTable.append(",\n");
                });

        createTable.replace(createTable.length() - 2, createTable.length() - 1, "");
        createTable.append(");");


        try (Statement statement = connection.createStatement()){
            statement.execute(String.valueOf(createTable));
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
