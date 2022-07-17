package database.service;

import database.DataSource;

import java.sql.*;

public class DataBaseServiceImpl implements DataBaseService{

    private static Statement statement;
    private static Connection connection;
    private static DataBaseService dbService = null;

    private DataBaseServiceImpl() {
    }

    public static DataBaseService init () throws SQLException {
        if (dbService == null){
            initDataBase();
            dbService = new DataBaseServiceImpl();
        }
        return dbService;
    }

    private static void initDataBase() throws SQLException {
        connection = DataSource.getConnection();
        statement = connection.createStatement();
    }

    @Override
    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public ResultSet readData(String inputSqlQuery) throws SQLException {
        return statement.executeQuery(inputSqlQuery);
    }

    @Override
    public void updateData(String inputSqlQuery) throws SQLException {
        statement.executeUpdate(inputSqlQuery);
    }

    @Override
    public void deleteData(String inputSqlQuery) throws SQLException {
        statement.execute(inputSqlQuery);
    }
}