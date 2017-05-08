package GUI;

import com.sun.org.apache.regexp.internal.RE;
import old.school.Man;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;

/**
 * Created by slavik on 03.05.17.
 */
public enum Button {
    /**
     * Команда: remove_greater_key.
     * Удаляет из коллекции все элементы, ключ которых превышает заданный.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     */
    REMOVE_GREATER_KEY {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

                updateRow = statement.executeUpdate("DELETE FROM people WHERE id> " + key);
                connection.commit();
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            } catch (IllegalArgumentException e) {
                msgToClient = "Key is not correct";
            }
            return updateRow;
        }
    },


    /**
     * Команда remove_lower.
     * Удаляет из коллекции все элементы, ключ которых меньше, чем заданный.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 1.0
     */
    REMOVE_LOWER_KEY {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

                updateRow = statement.executeUpdate("DELETE FROM people WHERE id < " + key);
                connection.commit();
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            } catch (IllegalArgumentException e) {
                msgToClient = "Key is not correct";
            }
            return updateRow;
        }
    },


    /**
     * Команда remove.
     * Удаляет элемент из коллекции по его ключу.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     */
    REMOVE_WITH_KEY {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

                updateRow = statement.executeUpdate("DELETE FROM people WHERE id = " + key);
                connection.commit();
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            } catch (IllegalArgumentException e) {
                msgToClient = "Key is not correct";
            }
            return updateRow;
        }
    },


    /**
     * Команда remove_greater.
     * Удаляет из коллекции все элементы, превышающие заданный.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     * @since 1.0
     */
    REMOVE_GREATER {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int age = newData.values().iterator().next().getAge();

                updateRow = statement.executeUpdate("DELETE FROM people WHERE age > " + age);

                connection.commit();
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            }
            return updateRow;
        }
    },


    /**
     * Команда remove_all.
     * Удалят из коллекции все элементы, эквивалентные заданному.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     * @since 1.0
     */
    REMOVE_ALL {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int age = newData.values().iterator().next().getAge();

                updateRow = statement.executeUpdate("DELETE FROM people WHERE age = " + age);

                connection.commit();
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            }
            return updateRow;
        }
    },


    /**
     * Команда remove_lower.
     * Удаляет из коллекции все элементы, меньшие, чем заданный.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     * @since 1.0
     */
    REMOVE_LOWER_OBJECT {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int age = newData.values().iterator().next().getAge();

                updateRow = statement.executeUpdate("DELETE FROM people WHERE age < " + age);

                connection.commit();
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            }
            return updateRow;
        }
    },


    /**
     * Команда: add_if_max.
     * Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     */
    ADD_IF_MAX {
        private int updateRow;
        private boolean isMax = true;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            try {
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT AGE " +
                        "FROM people " +
                        "ORDER BY AGE;");

                Iterator<Man> iterator = newData.values().iterator();
                int age = iterator.next().getAge();

                if (resultSet.getFetchSize() != 0) {
                    isMax = resultSet.getInt(1) > age;
                }

                this.updateRow = isMax ?
                        insertPeopleQueryExecute(connection, newData) :
                        0;

                msgToClient = isMax ?
                        "Object added" :
                        "Object is not max";

            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            }
            return updateRow;
        }
    },


    /**
     * Команда add_if_min.
     * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 2.0
     */
    ADD_IF_MIN {
        private int updateRow;
        private boolean isMin = true;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            try {
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT AGE " +
                        "FROM people " +
                        "ORDER BY AGE;");

                Iterator<Man> iterator = newData.values().iterator();
                int age = iterator.next().getAge();

                if (resultSet.getFetchSize() != 0) {
                    isMin = resultSet.getInt(1) > age;
                }

                this.updateRow = isMin ?
                        insertPeopleQueryExecute(connection, newData) :
                        0;

                msgToClient = isMin ?
                        "Object added" :
                        "Object is not min";

            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            }
            return updateRow;
        }
    },


    /**
     * Команда import.
     * добавляет в коллекцию все данные из файла.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     */
    IMPORT_ALL_FROM_FILE {
        int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Object added";
            try {
                removeFromNewDataDuplicate(connection, newData);
                updateRow = insertPeopleQueryExecute(connection, newData);
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            } catch (NumberFormatException e) {
                msgToClient = "Key is not correct";
            }
            return updateRow;
        }

        private void removeFromNewDataDuplicate(Connection connection, Map<String, Man> newData) throws SQLException {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT ID FROM people");
            while (resultSet.next()) {
                if (newData.containsKey(String.valueOf(resultSet.getInt(1)))) {
                    newData.remove(String.valueOf(resultSet.getInt(1)));
                }
            }

        }
    },


    /**
     * Команда insert.
     * Добавляет новый элемент с заданным ключом.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     */
    INSERT_NEW_OBJECT {
        private boolean isInDB;
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            try {
                Statement statement = connection.createStatement();

                int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

                isInDB = statement.executeQuery("SELECT ID " +
                        "FROM people " +
                        "WHERE ID = " + key).next();


                msgToClient = !isInDB ? "Object added" : "Object already in DB";

                updateRow = !isInDB ?
                        insertNewRowQuery(connection, newData) :
                        0;

            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            } catch (NumberFormatException e) {
                msgToClient = "Key is not correct";
            }
            return updateRow;
        }
    },


    /**
     * Команда clear.
     * Очищает коллекцию.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     * @since 1.0
     */
    CLEAR {
        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            try {
                Statement statement = connection.createStatement();
                msgToClient = "Database cleared";
                return statement.executeUpdate("DELETE FROM people;");
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            }
            return 0;
        }
    },


    /**
     * Команда load.
     * Загружает дефолтные объекты типа {@link Storage} данные в коллекцию.
     *
     * @param peopleTree Ожидается TreeView<Container> для изменения содержимого
     * @version 3.0
     */
    LOAD {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            try {
                connection.setAutoCommit(false);

                CLEAR.execute(connection, family, newData);

                updateRow = INSERT_NEW_OBJECT.execute(connection, family, newData);

                connection.commit();
                msgToClient = "Default data was loaded";
            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            }
            return updateRow;
        }
    },


    READ {

    },


    UPDATE {
        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            try {
                PreparedStatement statement = connection.prepareStatement(UPDATE_PEOPLE_NAME_QUERY);


                statement.setString(1, newData.values().iterator().next().getName());
                statement.setInt(2, Integer.parseInt(newData.keySet().iterator().next()));

                statement.executeUpdate();

            } catch (SQLException e) {
                msgToClient = "Could not connect to DB";
            } catch (NumberFormatException e) {
                msgToClient = "Key is not correct";
            }
            return 0;
        }
    };


    private static final String INSERT_PEOPLE_QUERY =
            "INSERT INTO PEOPLE(AGE, NAME) VALUES (?,?);";
    private static final String INSERT_NEW_ROW_QUERY =
            "INSERT INTO PEOPLE VALUES(?,?,?)";
    private static final String UPDATE_PEOPLE_NAME_QUERY =
            "UPDATE PEOPLE SET name = ? WHERE id = ?;";
    private static String msgToClient;

    public static String getMsgToClient() {
        return msgToClient;
    }

    public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
        return -1;
    }


    public int insertPeopleQueryExecute(Connection connection, Map<String, Man> newData) throws SQLException {
        int updateRow = 0;

        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PEOPLE_QUERY);
        for (Map.Entry<String, Man> entry : newData.entrySet()) {
            preparedStatement.setInt(1, entry.getValue().getAge());
            preparedStatement.setString(2, entry.getValue().getName());
            updateRow += preparedStatement.executeUpdate();
        }
        connection.commit();

        return updateRow;
    }

    public int insertNewRowQuery(Connection connection, Map<String, Man> newData) {
        int updateRow = 0;
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_ROW_QUERY);

            for (Map.Entry<String, Man> entry : newData.entrySet()) {
                preparedStatement.setInt(1, Integer.parseInt(entry.getKey()));
                preparedStatement.setInt(2, entry.getValue().getAge());
                preparedStatement.setString(3, entry.getValue().getName());
                updateRow += preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23514")) {
                msgToClient = "Age should be positive";
            }
        }
        return updateRow;
    }
}