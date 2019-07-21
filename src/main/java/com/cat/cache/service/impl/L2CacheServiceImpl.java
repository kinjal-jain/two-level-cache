package com.cat.cache.service.impl;

import com.cat.cache.domain.CacheObject;
import com.cat.cache.service.ICacheService;
import com.google.gson.Gson;
import java.sql.*;

public class L2CacheServiceImpl implements ICacheService {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static Gson gsonConverter = new Gson();

    /**
     * This function is used to retrieve the Connection for in memory database which is an instance of H2 Database.
     * @return
     */
    private static Connection getDatabaseConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    /**
     * This function is used to setup the in memory database, creates a Cache table with id as Key (primary Key)
     * and Value as a Stringified Json of the Value object.
     *
     * @throws SQLException
     */
    public static void run() throws SQLException {
        Connection connection = getDatabaseConnection();
        String dropTable = "DROP TABLE IF EXISTS CACHE";
        String createTable = "CREATE TABLE CACHE(id varchar(255) primary key, value varchar(255))";

        Statement stmt = null;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.execute(dropTable);
            stmt.execute(createTable);
            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    /**
     * This function calls the in memory database and retrieves the value for the key if present and returns it.
     * @param key
     * @return the Object(Value) corresponding to the key or an error message that the value for this key is not present.
     */
    @Override
    public Object get(String key) {
        Connection connection = getDatabaseConnection();
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String query = "SELECT * FROM CACHE WHERE ID='"+ key + "'";
            ResultSet rs = statement.executeQuery(query);
            String value = "";
            while (rs.next()) {
                System.out.println("Id " + rs.getString("id") + " Value as CacheObject " + rs.getString("value"));
                value = rs.getString("value");
            }
            if(value!=null || !value.isEmpty()) {
                CacheObject object = gsonConverter.fromJson(value, CacheObject.class);
                return object.getValue();
            }
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Value not found for Key : " + key;
    }

    /**
     * This function persists the key value pair in the in memory database.
     * @param key
     * @param value
     */
    @Override
    public void put(String key, Object value) {
        String cacheObjectString = gsonConverter.toJson(value);
        Connection connection = getDatabaseConnection();
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String query = "INSERT INTO CACHE(id, value) VALUES('"+ key +"','" + cacheObjectString + "')";
            statement.execute(query);
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
