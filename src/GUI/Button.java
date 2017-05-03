package GUI;

import old.school.Man;

import java.io.PrintWriter;
import java.sql.*;
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
    REMOVE_GRATER_KEY {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT ID FROM people");

                int key = Integer.parseInt(newData.keySet().iterator().next());
                while (resultSet.next()) {
                    if (resultSet.getInt(1) > key) {
                        resultSet.deleteRow();
                        updateRow++;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
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
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT ID FROM people");

                int key = Integer.parseInt(newData.keySet().iterator().next());
                while (resultSet.next()) {
                    if (resultSet.getInt(1) < key) {
                        resultSet.deleteRow();
                        updateRow++;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
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
            msgToClient = "Object removed";
            try {
                return removePeopleQueryExecute(connection, newData);
            } catch (SQLException e) {
                e.printStackTrace();
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
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM people");

                int age = newData.values().iterator().next().getAge();
                while (resultSet.next()) {
                    if (resultSet.getInt(2) > age) {
                        resultSet.deleteRow();
                        updateRow++;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                msgToClient = "Key is not correct";
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
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM people");

                int age = newData.values().iterator().next().getAge();
                while (resultSet.next()) {
                    if (resultSet.getInt(2) == age) {
                        resultSet.deleteRow();
                        updateRow++;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                msgToClient = "Key is not correct";
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
    REMOVE_LOVER_OBJECT {
        private int updateRow;

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "Objects removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM people");

                int age = newData.values().iterator().next().getAge();
                while (resultSet.next()) {
                    if (resultSet.getInt(2) < age) {
                        resultSet.deleteRow();
                        updateRow++;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                msgToClient = "Key is not correct";
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

                ResultSet resultSet = statement.executeQuery("SELECT AGE FROM people;");
                Iterator<Man> iterator = newData.values().iterator();
                int age = iterator.next().getAge();

                while (resultSet.next()) {
                    if (age <= resultSet.getInt(1)) {
                        isMax = false;
                    }
                }

                this.updateRow = isMax ?
                        insertPeopleQueryExecute(connection, newData) :
                        0;

                msgToClient = isMax ?
                        "Object is not min" :
                        "Object added";

            } catch (SQLException e) {
                e.printStackTrace();
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

                ResultSet resultSet = statement.executeQuery("SELECT AGE FROM people;");
                Iterator<Man> iterator = newData.values().iterator();
                int age = iterator.next().getAge();

                while (resultSet.next()) {
                    if (age >= resultSet.getInt(1)) {
                        isMin = false;
                    }
                }

                this.updateRow = isMin ?
                        insertPeopleQueryExecute(connection, newData) :
                        0;

                msgToClient = isMin ?
                        "Object is not min" :
                        "Object added";

            } catch (SQLException e) {
                e.printStackTrace();
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
                e.printStackTrace();
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
            msgToClient = "Object added";
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT ID FROM people;");
                while (resultSet.next()) {
                    if (newData.containsKey(resultSet.getString(1))) {
                        isInDB = true;
                        msgToClient = "Object already in DB";
                    }
                }

                updateRow = !isInDB ? insertNewRowQuery(connection, newData) : 0;

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                msgToClient = "Key is not correct";
            }
            return updateRow;
        }
    };


    private static final String INSERT_PEOPLE_QUERY =
            "INSERT INTO PEOPLE(AGE, NAME) VALUES (?,?);";
    private static final String REMOVE_PEOPLE_QUERY =
            "DELETE FROM PEOPLE WHERE id = ?";
    private static final String INSERT_NEW_ROW_QUERY =
            "INSERT INTO PEOPLE VALUES(?,?,?)";
    private static final String UPDATE_PEOPLE_NAME_QUERY =
            "UPDATE PEOPLE SET name = ?;";
    private static String msgToClient;

    public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
        return -1;
    }


    public int removePeopleQueryExecute(Connection connection, Map<String, Man> newData) throws SQLException, IllegalArgumentException {
        int updateRow = 0;

        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_PEOPLE_QUERY);
        for (Map.Entry<String, Man> entry : newData.entrySet()) {
            preparedStatement.setInt(1, Integer.parseInt(entry.getKey()));
            updateRow += preparedStatement.executeUpdate();
        }
        connection.commit();

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

    public int insertNewRowQuery(Connection connection, Map<String, Man> newData) throws SQLException {
        int updateRow = 0;
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_ROW_QUERY);
        for (Map.Entry<String, Man> entry : newData.entrySet()) {
            preparedStatement.setInt(1, Integer.parseInt(entry.getKey()));
            preparedStatement.setInt(2, entry.getValue().getAge());
            preparedStatement.setString(3, entry.getValue().getName());
            updateRow += preparedStatement.executeUpdate();
        }
        connection.commit();
        return updateRow;
    }
}