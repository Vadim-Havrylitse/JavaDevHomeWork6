package service;

import dto.model.CustomersDto;
import dto.model.CustomersOutputDto;
import util.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomersService extends DtoFactory<CustomersDto, CustomersOutputDto>{
    private final PreparedStatement setCustomer;
    private final PreparedStatement dropCustomer;
    private final PreparedStatement getCustomer;
    private final PreparedStatement getAllCustomers;
    private final String updateCustomer;

    {
        setCustomer = getPreparedStatement("INSERT INTO customers (name, surname) VALUES (?,?);");
        dropCustomer = getPreparedStatement("DELETE FROM customers WHERE id = ?;");
        getCustomer = getPreparedStatement("SELECT * FROM customers WHERE id = ?;");
        getAllCustomers = getPreparedStatement("SELECT * FROM customers;");
        updateCustomer = "UPDATE customers SET %s WHERE id = %s;";
    }

    @Override
    public ApiResponse save(CustomersDto dto) {
        try{
            System.out.println("dto.toString() = " + dto.toString());
            setCustomer.setString(1, dto.getName());
            setCustomer.setString(2, dto.getSurname());

            if (setCustomer.executeUpdate()>0){
                ResultSet generatedKeys = setCustomer.getGeneratedKeys();
                generatedKeys.next();
                long cusId = generatedKeys.getLong(1);
                return new ApiResponse(HttpServletResponse.SC_OK, "Ваш разбатотчик успешно добавлен в базу. ID = " + cusId);
            } else {
                return FAIL_API_RESPONSE;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }

    @Override
    public ApiResponse delete(Long index) {
        try {
            dropCustomer.setLong(1, index);
            return  dropCustomer.executeUpdate()>0 ?  OK_API_RESPONSE:FAIL_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }

    @Override
    public CustomersOutputDto read(Long index) {
        try {
            DtoParsingService dtoParsingService = new DtoParsingService();

            getCustomer.setLong(1, index);
            ResultSet resultSet = getCustomer.executeQuery();
            return dtoParsingService.convertResulSetToDto(resultSet, CustomersOutputDto.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CustomersOutputDto();
    }

    @Override
    public List<CustomersOutputDto> readAll() {
        List<CustomersOutputDto> result = new ArrayList<>();
        try {
            ResultSet resultSet = getAllCustomers.executeQuery();
            while (resultSet.next()){
                result.add(read(resultSet.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ApiResponse update(Long index, CustomersDto dto) {
        StringBuilder queryBuilder = new StringBuilder();
        try{
            if (dto.getName() != null){
                queryBuilder.append("name = \"").append(dto.getName()).append("\", ");
            }
            if (dto.getSurname() != null){
                queryBuilder.append("surname = \"").append(dto.getSurname()).append("\", ");
            }

            if (queryBuilder.length() > 0){
                queryBuilder.deleteCharAt(queryBuilder.length()-2);
                String query = String.format(updateCustomer, queryBuilder, index);
                dataBaseService.updateData(query);
            }
            return OK_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }
}
