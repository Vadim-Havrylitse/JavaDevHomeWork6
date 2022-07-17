package database.service;

import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataBaseService {

    PreparedStatement getPreparedStatement(String query) throws SQLException;
    ResultSet readData(String inputSqlQuery) throws SQLException;
    void updateData(String inputSqlQuery) throws SQLException;
    void deleteData(String inputSqlQuery) throws SQLException;

    @SneakyThrows
    static DataBaseService of() {
        return DataBaseServiceImpl.init();
    }


}
