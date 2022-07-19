package service;

import database.service.DataBaseService;
import util.ApiEntity;
import lombok.SneakyThrows;
import util.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DtoFactory <I, O> extends DtoParsingService {
    protected final DataBaseService dataBaseService = DataBaseService.of();
    protected static final ApiResponse FAIL_API_RESPONSE = new ApiResponse(HttpServletResponse.SC_BAD_REQUEST, "Что-то пошло не так. Проверьте введенные данные."); ;
    protected static final ApiResponse OK_API_RESPONSE = new ApiResponse(HttpServletResponse.SC_OK, "Все прошло успешно.");;

    protected DtoFactory(){}

    public static DtoFactory init(ApiEntity entityName){
        switch (entityName) {
            case SKILLS: return new SkillsService();
            case PROJECTS: return new ProjectsService();
            case COMPANIES: return new CompaniesService();
            case CUSTOMERS: return new CustomersService();
            default:
                return new DevelopersService();
        }
    }

    @SneakyThrows
    protected PreparedStatement getPreparedStatement(String query){
        return dataBaseService.getPreparedStatement(query);
    }

    public static List<String> getTablesName(){
        List<String> result = new ArrayList<>();
        for (ApiEntity entity: ApiEntity.values()){
            result.add(entity.name());
        }
        return result;
    }

    public ResultSet executeAnyReadQuery(String query){
        try {
            return dataBaseService.readData(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract ApiResponse save(I dto);
    public abstract ApiResponse delete(Long index);
    public abstract O read(Long index);
    public abstract List<O> readAll();
    public abstract ApiResponse update(Long index, I dto);
}