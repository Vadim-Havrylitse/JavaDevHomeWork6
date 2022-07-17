package dto.factory;

import dto.model.*;
import dto.service.DtoService;
import util.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectsService extends DtoFactory<ProjectsDto, ProjectsOutputDto>{
    private final PreparedStatement setProject;
    private final PreparedStatement dropProject;
    private final String  updateProject;
    private final PreparedStatement getAllProjects;
    private final PreparedStatement getProject;
    private final PreparedStatement getCompany;
    private final PreparedStatement getCustomer;

    {
        setProject = getPreparedStatement("INSERT INTO projects (name, budget, release_date, companies_id, customers_id) VALUES (?,?,?,?,?);");
        dropProject = getPreparedStatement("DELETE FROM projects WHERE id=?;");
        updateProject = "UPDATE projects SET %s WHERE id = %s;";
        getAllProjects = getPreparedStatement("SELECT * FROM projects;");
        getProject = getPreparedStatement("SELECT * FROM projects WHERE id=?;");
        getCompany = getPreparedStatement("SELECT * FROM companies WHERE id = ?;");
        getCustomer = getPreparedStatement("SELECT * FROM customers WHERE id = ?;");
    }

    @Override
    public ApiResponse save(ProjectsDto dto) {
        try{
            setProject.setString(1, dto.getName());
            setProject.setLong(2, dto.getBudget());
            if(dto.getReleaseDate() != null){
                setProject.setDate(3, Date.valueOf(dto.getReleaseDate()));
            }

            setProject.setLong(4, dto.getCompanyId());
            setProject.setLong(5, dto.getCustomerId());
            if(setProject.executeUpdate() > 0){
                ResultSet generatedKeys = setProject.getGeneratedKeys();
                generatedKeys.next();
                long devId = generatedKeys.getLong(1);
                return new ApiResponse(HttpServletResponse.SC_OK, "Ваш проект успешно добавлен в базу. ID = " + devId);
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
            dropProject.setLong(1,index);
            return  dropProject.executeUpdate()>0 ?  OK_API_RESPONSE:FAIL_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }

    @Override
    public ProjectsOutputDto read(Long index) {
        try {
            DtoService dtoService = new DtoService();

            getProject.setLong(1, index);
            ResultSet resultSet = getProject.executeQuery();
            ProjectsOutputDto projects = dtoService.convertResulSetToDto(resultSet, ProjectsOutputDto.class);

            getCompany.setLong(1, resultSet.getLong("companies_id"));
            ResultSet resultSet1 = getCompany.executeQuery();
            CompaniesOutputDto company = dtoService.convertResulSetToDto(resultSet1, CompaniesOutputDto.class);
            projects.setCompany(company);

            getCustomer.setLong(1, resultSet.getLong("customers_id"));
            ResultSet resultSet2 = getCustomer.executeQuery();
            CustomersOutputDto customer= dtoService.convertResulSetToDto(resultSet2, CustomersOutputDto.class);
            projects.setCustomer(customer);

            return projects;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return new ProjectsOutputDto();
    }

    @Override
    public List<ProjectsOutputDto> readAll() {
        List<ProjectsOutputDto> result = new ArrayList<>();
        try {
            ResultSet resultSet = getAllProjects.executeQuery();
            while (resultSet.next()){
                result.add(read(resultSet.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ApiResponse update(Long index, ProjectsDto dto) {
        StringBuilder queryBuilder = new StringBuilder();
        try {
            if (dto.getName() != null) {
                queryBuilder.append("name = '").append(dto.getName()).append("', ");
            }
            if (dto.getBudget() != null) {
                queryBuilder.append("budget = ").append(dto.getBudget()).append(", ");
            }
            if (dto.getReleaseDate() != null) {
                queryBuilder.append("release_date = '").append(Date.valueOf(dto.getReleaseDate()).toString()).append("', ");
            }
            if (dto.getCompanyId() != null) {
                queryBuilder.append("companies_id = ").append(dto.getCompanyId()).append(", ");
            }
            if (dto.getCustomerId() != null) {
                queryBuilder.append("customers_id = ").append(dto.getCustomerId()).append(", ");
            }

            if (queryBuilder.length() > 0) {
                queryBuilder.deleteCharAt(queryBuilder.length() - 2);
                String query = String.format(updateProject, queryBuilder, index);
                dataBaseService.updateData(query);
            }
            return OK_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }
}