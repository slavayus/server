package GUI;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import old.school.Man;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

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
            msgToClient = "message.server.objects.removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

                updateRow = statement.executeUpdate("DELETE FROM people WHERE id> " + key);
                connection.commit();
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
            } catch (IllegalArgumentException e) {
                msgToClient = "message.server.key.is.not.correct";
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
            msgToClient = "message.server.objects.removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

                updateRow = statement.executeUpdate("DELETE FROM people WHERE id < " + key);
                connection.commit();
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
            } catch (IllegalArgumentException e) {
                msgToClient = "message.server.key.is.not.correct";
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
            msgToClient = "message.server.objects.removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

                updateRow = statement.executeUpdate("DELETE FROM people WHERE id = " + key);
                connection.commit();
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
            } catch (IllegalArgumentException e) {
                msgToClient = "message.server.key.is.not.correct";
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
            msgToClient = "message.server.objects.removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int age = newData.values().iterator().next().getAge();

                updateRow = statement.executeUpdate("DELETE FROM people WHERE age > " + age);

                connection.commit();
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
            msgToClient = "message.server.objects.removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int age = newData.values().iterator().next().getAge();

                updateRow = statement.executeUpdate("DELETE FROM people WHERE age = " + age);

                connection.commit();
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
            msgToClient = "message.server.objects.removed";
            try {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();

                int age = newData.values().iterator().next().getAge();

                updateRow = statement.executeUpdate("DELETE FROM people WHERE age < " + age);

                connection.commit();
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
                        "message.server.object.added" :
                        "message.sever.object.is.not.max";

            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
                        "message.server.object.added" :
                        "message.sever.object.is.not.min";

            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
            msgToClient = "message.server.object.added";
            try {
                removeFromNewDataDuplicate(connection, newData);
                updateRow = insertPeopleQueryExecute(connection, newData);
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
            } catch (NumberFormatException e) {
                msgToClient = "message.server.key.is.not.correct";
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


                msgToClient = !isInDB ? "message.server.object.added" : "message.server.object.already.in.DB";

                updateRow = !isInDB ?
                        insertNewRowQuery(connection, newData) :
                        0;

            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
            } catch (NumberFormatException e) {
                msgToClient = "message.server.key.is.not.correct";
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
                msgToClient = "message.server.database.cleared";
                return statement.executeUpdate("DELETE FROM people;");
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
                msgToClient = "message.server.default.data.was.loaded";
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
                msgToClient = "message.server.could.not.connect.to.DB";
            } catch (NumberFormatException e) {
                msgToClient = "message.server.key.is.not.correct";
            }
            return 0;
        }
    },


    REGISTER {
        private String createTable = "CREATE TABLE USERS(" +
                "NAME TEXT NOT NULL," +
                "PASSWORD TEXT NOT NULL," +
                "MAIL TEXT ," +
                "FULL_VERSION BOOLEAN NOT NULL," +
                "PRIMARY KEY(NAME, PASSWORD)" +
                ");";

        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "message.server.this.user.already.exist";
            try {

                try (Statement createTableStatement = connection.createStatement()) {
                    createTableStatement.executeUpdate(createTable);
                } catch (SQLException e) {
//                    System.out.println(e.getMessage());
                }

                Map.Entry<String, Man> user = newData.entrySet().iterator().next();

                String searchQuery = "SELECT count(*) FROM users WHERE name = ? AND mail = ?;";
                PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
                searchStatement.setString(1, user.getValue().getName());
                searchStatement.setString(2, user.getKey());
                ResultSet resultSet = searchStatement.executeQuery();
                resultSet.next();
                if (resultSet.getInt(1) > 0) {
                    return 0;
                }
                searchStatement.close();


                String insertQuery = "INSERT INTO users VALUES (?,?,?);";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, user.getValue().getName());
                preparedStatement.setString(2, user.getKey());
                preparedStatement.setBoolean(3, user.getValue().getAge() == 2);
                preparedStatement.executeUpdate();

                msgToClient = "message.server.user.added";
                return 1;
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
            }
            return 0;
        }
    },

    /**
     * age 1 - limited version
     * age 2 - full version
     * <p>
     * key - mail
     * name - username
     */
    REGISTER_FULL {
        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "message.server.this.user.already.exist";
            try {
                Map.Entry<String, Man> user = newData.entrySet().iterator().next();

                String searchQuery = "SELECT count(*) FROM users WHERE name = ? AND password = ?;";
                PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
                searchStatement.setString(1, user.getValue().getName());
                searchStatement.setString(2, user.getKey());
                ResultSet resultSet = searchStatement.executeQuery();
                resultSet.next();
                if (resultSet.getInt(1) > 0) {
                    return 0;
                }
                searchStatement.close();

                String password = generatePassword(connection, user);

                String insertQuery = "INSERT INTO users VALUES (?,?,?,?);";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, user.getValue().getName());
                preparedStatement.setString(2, password);
                preparedStatement.setBoolean(3, user.getValue().getAge() == 2);
                preparedStatement.setString(4, user.getKey());
                preparedStatement.executeUpdate();

                sendMessage(user.getKey(), user.getValue().getName(), password);

                msgToClient = "message.server.password.sent.to.mail " + user.getKey();
                return 1;
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
            }
            return 0;
        }

        private void sendMessage(String username, String name, String password) {
            Properties props = new Properties();
            try (InputStream inputStream = Button.class.getResourceAsStream("/properties/mail.properties")) {
                props.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Session session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(props.getProperty("username"), props.getProperty("password"));
                            }
                        });

                // -- Create a new message --
                Message msg = new MimeMessage(session);

                // -- Set the FROM and TO fields --
                msg.setFrom(new InternetAddress(props.getProperty("username")));
                msg.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(username, false));
                msg.setSubject("Collection");

                msg.setText("Username - " + name + "\n" + "Password - " + password);
                msg.setSentDate(new java.util.Date());
                Transport.send(msg);
                System.out.println("Message sent.");
            } catch (MessagingException e) {
                msgToClient = "message.server.mail.is.not.correct";
            }
        }

        private String generatePassword(Connection connection, Map.Entry<String, Man> user) throws SQLException {
            ResultSet resultSet;
            StringBuilder randString;
            PreparedStatement preparedStatement;

            int count ;
            do{
                count =(int) (Math.random() * 30);
            }while (count<7);

            do {
                String symbols = "qwertyuiopasdfghjklzxcvbnm1234567890";
                randString = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    randString.append(symbols.charAt((int) (Math.random() * symbols.length())));
                }

                String insertQuery = "SELECT count(*) FROM users WHERE name = ? AND password = ? AND full_version = ? AND mail = ?;";
                preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, user.getValue().getName());
                preparedStatement.setString(2, user.getKey());
                preparedStatement.setBoolean(3, user.getValue().getAge() == 2);
                preparedStatement.setString(4, String.valueOf(randString));
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
            } while (resultSet.getInt(1) > 0);
            return String.valueOf(randString);
        }
    },


    /**
     * msgToClient : true - fullVersion; false - limitedVersion
     * return 1 if this user exist
     * 0 if this user doesn't exist
     */
    LOGIN {
        @Override
        public int execute(Connection connection, Map<String, Man> family, Map<String, Man> newData) {
            msgToClient = "message.server.user.not.found";
            try {
                Statement searchStatement = connection.createStatement();


                Map.Entry<String, Man> user = newData.entrySet().iterator().next();

                String searchQuery = "SELECT full_version FROM users WHERE name = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(searchQuery);
                preparedStatement.setString(1, user.getValue().getName());
                preparedStatement.setString(2, user.getKey());
                ResultSet resultSet = preparedStatement.executeQuery();

//                resultSet.next();
                if (resultSet.next()) {
                    msgToClient = String.valueOf(resultSet.getBoolean(1));
                    return 1;
                }
                searchStatement.close();
                return 0;
            } catch (SQLException e) {
                msgToClient = "message.server.could.not.connect.to.DB";
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
                msgToClient = "message.server.age.should.be.positive";
            }
        }
        return updateRow;
    }
}