package service;

import dto.model.CompaniesDto;
import dto.model.CompaniesOutputDto;
import util.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompaniesService extends DtoFactory<CompaniesDto, CompaniesOutputDto> {
    private final PreparedStatement setCompany;
    private final PreparedStatement dropCompany;
    private final PreparedStatement getCompany;
    private final PreparedStatement getAllCompanies;
    private final String updateCompany;

    {
        setCompany = getPreparedStatement("INSERT INTO companies (name, country, city) VALUES (?,?,?);");
        dropCompany = getPreparedStatement("DELETE FROM companies WHERE id = ?;");
        getCompany = getPreparedStatement("SELECT * FROM companies WHERE id = ?;");
        getAllCompanies = getPreparedStatement("SELECT * FROM companies;");
        updateCompany = "UPDATE companies SET %s WHERE id = %s;";
    }

    @Override
    public ApiResponse save(CompaniesDto dto) {
        try{
            setCompany.setString(1, dto.getName());
            setCompany.setString(2, dto.getCountry());
            setCompany.setString(3, dto.getCity());

            if (setCompany.executeUpdate()>0){
                ResultSet generatedKeys = setCompany.getGeneratedKeys();
                generatedKeys.next();
                long comId = generatedKeys.getLong(1);
                return new ApiResponse(HttpServletResponse.SC_OK, "Ваша компания успешно добавлена в базу. ID = " + comId);
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
            dropCompany.setLong(1, index);
            return  dropCompany.executeUpdate()>0 ?  OK_API_RESPONSE:FAIL_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }

    @Override
    public CompaniesOutputDto read(Long index) {
        try {
            DtoParsingService dtoParsingService = new DtoParsingService();

            getCompany.setLong(1, index);
            ResultSet resultSet = getCompany.executeQuery();
            return dtoParsingService.convertResulSetToDto(resultSet, CompaniesOutputDto.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CompaniesOutputDto();
    }

    @Override
    public List<CompaniesOutputDto> readAll() {
        List<CompaniesOutputDto> result = new ArrayList<>();
        try {
            ResultSet resultSet = getAllCompanies.executeQuery();
            while (resultSet.next()){
                result.add(read(resultSet.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ApiResponse update(Long index, CompaniesDto dto) {
        StringBuilder queryBuilder = new StringBuilder();
        try{
            if (dto.getName() != null){
                queryBuilder.append("name = \"").append(dto.getName()).append("\", ");
            }
            if (dto.getCity() != null){
                queryBuilder.append("city = \"").append(dto.getCity()).append("\", ");
            }
            if (dto.getCountry() != null){
                queryBuilder.append("country = \"").append(dto.getCountry()).append("\", ");
            }
            if (queryBuilder.length() > 0){
                queryBuilder.deleteCharAt(queryBuilder.length()-2);
                String query = String.format(updateCompany, queryBuilder, index);
                dataBaseService.updateData(query);
            }
            return OK_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }
}