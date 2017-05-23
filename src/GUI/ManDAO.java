package GUI;

import old.school.Man;
import old.school.People;

import java.sql.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public Map<String, Man> select(Man man) {
        return null;
    }


    @Override
    public int insert(Map<String, Man> family, boolean withKey) {
        return withKey ? insertNewRowQuery(family) : insertPeopleQueryExecute(family);
    }


    private int insertPeopleQueryExecute(Map<String, Man> newData) {
        final String INSERT_PEOPLE_QUERY =
                "INSERT INTO PEOPLE(AGE, NAME, CREATE_DATE) VALUES (?,?,?);";
        int updateRow = 0;

        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }

            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PEOPLE_QUERY);

            for (Map.Entry<String, Man> entry : newData.entrySet()) {
                preparedStatement.setInt(1, entry.getValue().getAge());
                preparedStatement.setString(2, entry.getValue().getName());
                preparedStatement.setTimestamp(3, new Timestamp(entry.getValue().getTime().toInstant().getEpochSecond() * 1000L));
                updateRow += preparedStatement.executeUpdate();
            }
            msgResult = "message.server.object.added";
            connection.commit();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23514")) {
                msgResult = "message.server.age.should.be.positive";
            }
        } catch (NullPointerException ex) {
            msgResult = "message.server.could.not.connect.to.DB";
        }
        return updateRow;
    }


    private int insertNewRowQuery(Map<String, Man> newData) {
        final String INSERT_NEW_ROW_QUERY =
                "INSERT INTO PEOPLE VALUES(?,?,?,?)";
        int updateRow = 0;
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_ROW_QUERY);

            for (Map.Entry<String, Man> entry : newData.entrySet()) {
                preparedStatement.setInt(1, Integer.parseInt(entry.getKey()));
                preparedStatement.setInt(2, entry.getValue().getAge());
                preparedStatement.setString(3, entry.getValue().getName());
                preparedStatement.setTimestamp(4, new Timestamp(entry.getValue().getTime().toInstant().getEpochSecond() * 1000L));
                updateRow += preparedStatement.executeUpdate();
            }

            connection.commit();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23514")) {
                msgResult = "message.server.age.should.be.positive";
            }
        } catch (NullPointerException ex) {
            msgResult = "message.server.could.not.connect.to.DB";
        }
        return updateRow;
    }


    @Override
    public int remove(Man man) {
        return 0;
    }


    @Override
    public void update(Map<String, Man> newData) {
        final String UPDATE_PEOPLE_NAME_QUERY =
                "UPDATE PEOPLE SET name = ? WHERE id = ?;";
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }

            PreparedStatement statement = connection.prepareStatement(UPDATE_PEOPLE_NAME_QUERY);

            statement.setString(1, newData.values().iterator().next().getName());
            statement.setInt(2, Integer.parseInt(newData.keySet().iterator().next()));

            statement.executeUpdate();

        } catch (SQLException | NullPointerException e) {
            msgResult = "message.server.could.not.connect.to.DB";
        } catch (NumberFormatException e) {
            msgResult = "message.server.key.is.not.correct";
        }
    }


    public Map<String, Man> selectAll() {
        Map<String, Man> dataFromDB = new LinkedHashMap<>();


        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM people");
            while (resultSet.next()) {
                People people = new People(resultSet.getInt(2), resultSet.getString(3));
                people.setTime(ZonedDateTime.ofInstant(resultSet.getTimestamp(4).toInstant(), ZoneOffset.UTC));
                dataFromDB.put((String.valueOf(resultSet.getInt(1))), people);
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

    public boolean searchWithID(Map<String, Man> newData) {
        boolean flag = false;
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }
            Statement statement = connection.createStatement();

            int key = Integer.parseInt(newData.entrySet().iterator().next().getKey());

            flag = statement.executeQuery("SELECT ID " +
                    "FROM people " +
                    "WHERE ID = " + key).next();

            msgResult = !flag ?
                    "message.server.object.added" :
                    "message.server.object.already.in.DB";


            statement.close();
            connection.close();
        } catch (SQLException | NullPointerException e) {
            msgResult = "message.server.could.not.connect.to.DB";
        } catch (NumberFormatException e) {
            msgResult = "message.server.key.is.not.correct";
        }
        return flag;
    }

    public int clearDB() {
        int modifiedRow = 0;
        try {
            Connection connection = getConnection();
            if (connection == null) {
                throw new NullPointerException();
            }

            Statement statement = connection.createStatement();
            modifiedRow = statement.executeUpdate("DELETE FROM people;");
            msgResult = "message.server.database.cleared";

        } catch (NullPointerException e) {
            msgResult = "message.server.age.should.be.positive";
        } catch (SQLException e) {
            msgResult = "message.server.could.not.connect.to.DB";
        }
        return modifiedRow;
    }
}
