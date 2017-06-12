package GUI;

import old.school.Man;
import old.school.People;
import old.school.annotation.Column;
import old.school.annotation.Entity;
import old.school.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static connectDB.ConnectDB.getConnection;

/**
 * Created by slavik on 23.05.17.
 */
public class ManDAO implements CRUD {
    private String msgResult;


    public String getMsgResult() {
        return msgResult;
    }


    @Override
    public Map<String, Man> select(Man man) throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(Man man) {

        Entity entity = man.getClass().getSuperclass().getAnnotation(Entity.class);

        LinkedList<Field> primaryKeys = new LinkedList<>();
        Arrays.stream(man.getClass().getSuperclass().getDeclaredFields())
                .filter(field -> field.getAnnotation(PrimaryKey.class) != null)
                .forEach(primaryKeys::add);

        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(entity.tableName());
        query.append("\n WHERE ( ");

        primaryKeys
                .forEach(field -> {
                    query.append("  ");
                    query.append(field.getName());
                    query.append(" = ");
                    Arrays.stream(man.getClass().getSuperclass()
                            .getMethods())
                            .filter(method -> (method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3)))
                            .filter(method -> method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
                            .forEach(method -> {
                                try {
                                    query.append(method.invoke(man));
                                } catch (IllegalAccessException | InvocationTargetException e1) {
                                    e1.printStackTrace();
                                }
                            });
                    query.append(", ");
                });
        query.replace(query.length() - 2, query.length() - 1, "");
        query.append(");");

        return executeQuery(query.toString());
    }



    @Override
    public int insert(Man man, boolean withKey) {
        return withKey ?
                insertPeopleWithKey(man) :
                insertPeopleWithoutKey(man);
    }

    private int insertPeopleWithoutKey(Man man) {

        Entity entity = man.getClass().getSuperclass().getAnnotation(Entity.class);

        LinkedList<Field> columns = new LinkedList<>();
        Arrays.stream(man.getClass().getSuperclass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Column.class) != null)
                .filter(field -> field.getAnnotation(PrimaryKey.class) == null)
                .forEach(columns::add);


        List<Column> annotationColumns = new LinkedList<>();
        Arrays.stream(man.getClass().getSuperclass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Column.class) != null)
                .filter(field -> field.getAnnotation(PrimaryKey.class) == null)
                .forEach(field -> annotationColumns.add(field.getAnnotation(Column.class)));


        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(entity.tableName());
        query.append(" (");
        annotationColumns
                .forEach(column -> {
                    query.append(column.atributeName());
                    query.append(", ");
                });

        query.replace(query.length() - 2, query.length() - 1, "");
        query.append(")");

        query.append("\n VALUES ( ");

        columns
                .forEach(field -> {
                    Arrays.stream(man.getClass().getSuperclass()
                            .getMethods())
                            .filter(method -> (method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3)))
                            .filter(method -> method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
                            .forEach(method -> invokeMethod(method, man, query));
                    query.append(", ");
                });
        query.replace(query.length() - 2, query.length() - 1, "");
        query.append(");");

        return executeQuery(query.toString());
    }

    private void invokeMethod(Method method, Man man, StringBuilder query) {
        try {
            boolean flag = method.getReturnType() == String.class || method.getReturnType() == ZonedDateTime.class;
            if (flag) {
                query.append(" '");
            }
            if (method.getReturnType() == ZonedDateTime.class) {
                query.append(new Timestamp(((ZonedDateTime) (method.invoke(man))).toInstant().getEpochSecond() * 1000L));
            } else {
                query.append(method.invoke(man));
            }
            if (flag) {
                query.append("' ");
            }
        } catch (IllegalAccessException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }

    private int executeQuery(String query) {
        int modifiedRows = 0;
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }

            PreparedStatement statement = connection.prepareStatement(query);

            modifiedRows = statement.executeUpdate();
            msgResult = "message.server.object.added";
        } catch (SQLException | NullPointerException e) {
            msgResult = "message.server.could.not.connect.to.DB";
        } catch (NumberFormatException e) {
            msgResult = "message.server.key.is.not.correct";
        }
        return modifiedRows;
    }

    private int insertPeopleWithKey(Man man) {

        Entity entity = man.getClass().getSuperclass().getAnnotation(Entity.class);

        LinkedList<Field> columns = new LinkedList<>();
        Arrays.stream(man.getClass().getSuperclass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Column.class) != null)
                .forEach(columns::add);


        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(entity.tableName());
        query.append("\n VALUES ( ");

        columns
                .forEach(field -> {
                    Arrays.stream(man.getClass().getSuperclass()
                            .getMethods())
                            .filter(method -> (method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3)))
                            .filter(method -> method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
                            .forEach(method -> {
                                try {
                                    boolean flag = (method.getReturnType() == String.class || method.getReturnType() == ZonedDateTime.class) && field.getAnnotation(PrimaryKey.class) == null;
                                    if (flag) {
                                        query.append(" '");
                                    }
                                    if (method.getReturnType() == ZonedDateTime.class) {
                                        query.append(new Timestamp(((ZonedDateTime) (method.invoke(man))).toInstant().getEpochSecond() * 1000L));
                                    } else {
                                        query.append(method.invoke(man));
                                    }
                                    if (flag) {
                                        query.append("' ");
                                    }
                                } catch (IllegalAccessException | InvocationTargetException e1) {
                                    e1.printStackTrace();
                                }

                            });
                    query.append(", ");
                });
        query.replace(query.length() - 2, query.length() - 1, "");
        query.append(");");

        return executeQuery(query.toString());
    }


    @Override
    public void update(Man man) {
        Entity entity = man.getClass().getSuperclass().getAnnotation(Entity.class);

        List<Column> annotationColumns = new LinkedList<>();
        Arrays.stream(man.getClass().getSuperclass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Column.class) != null)
                .filter(field -> field.getAnnotation(PrimaryKey.class) == null)
                .forEach(field -> {
                    annotationColumns.add(field.getAnnotation(Column.class));
                });


        List<PrimaryKey> primaryKeys = new ArrayList<>();
        Arrays.stream(man.getClass().getSuperclass().getDeclaredFields())
                .filter(field -> field.getAnnotation(PrimaryKey.class) != null)
                .forEach(field -> {
                    primaryKeys.add(field.getAnnotation(PrimaryKey.class));
                });

        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(entity.tableName());
        query.append(" SET \n");

        annotationColumns
                .forEach(column -> {
                    query.append("  ");
                    query.append(column.atributeName());
                    query.append(" = ");
                    Arrays.stream(man.getClass().getSuperclass()
                            .getMethods())
                            .filter(method -> (method.getName().startsWith("get")) && (method.getName().length() == (column.fieldName().length() + 3)))
                            .filter(method -> method.getName().toLowerCase().endsWith(column.fieldName().toLowerCase()))
                            .forEach(method -> invokeMethod(method, man, query));
                    query.append(", ");
                });

        query.replace(query.length() - 2, query.length() - 1, "");

        query.append(" WHERE ");
        primaryKeys.stream()
                .filter(Objects::nonNull)
                .forEach(primaryKey -> {
                    query.append(primaryKey.columName());
                    query.append("=");
                    Arrays.stream(man.getClass().getSuperclass()
                            .getMethods())
                            .filter(method -> (method.getName().startsWith("get")) && (method.getName().length() == (primaryKey.columName().length() + 3)))
                            .filter(method -> method.getName().toLowerCase().endsWith(primaryKey.columName().toLowerCase()))
                            .forEach(method -> {
                                try {
                                    query.append(method.invoke(man));
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }

                            });

                    query.append(", ");
                });

        query.replace(query.length() - 2, query.length() - 1, "");
        query.append(";");

        executeQuery(query.toString());
    }


    public Map<String,Man> list() {
        Entity entity = Man.class.getAnnotation(Entity.class);

        Map<String,Man> dataFromDB = new LinkedHashMap<>();


        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+entity.tableName());
            while (resultSet.next()) {
                People people = new People(resultSet.getInt(2), resultSet.getString(3));
                people.setTime(ZonedDateTime.ofInstant(resultSet.getTimestamp(4).toInstant(), ZoneOffset.UTC));
                dataFromDB.put(String.valueOf(resultSet.getInt(1)), people);
            }

            resultSet.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            msgResult = "message.server.could.not.connect.to.DB";
        }
        return dataFromDB;
    }

    int clearDB() {
        int modifiedRow = 0;
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }

            Statement statement = connection.createStatement();
            modifiedRow = statement.executeUpdate("DELETE FROM people;");
            msgResult = "message.server.database.cleared";

        } catch (NullPointerException | SQLException e) {
            msgResult = "message.server.could.not.connect.to.DB";
        }
        return modifiedRow;
    }
}
