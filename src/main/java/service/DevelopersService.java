package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dto.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import util.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DevelopersService extends DtoFactory<DevelopersDto, DevelopersOutputDto>{
    private final PreparedStatement setDeveloper;
    private final PreparedStatement dropDevelopers;
    private final String  updateDevelopers;
    private final PreparedStatement setDevelopersProjects;
    private final PreparedStatement setDevelopersSkills;
    private final PreparedStatement getAllDevelopers;
    private final PreparedStatement getDeveloper;
    private final PreparedStatement getProjectFromDevId;
    private final PreparedStatement getSkillFromDevId;
    private final PreparedStatement getCompany;

    {
        setDeveloper = getPreparedStatement("INSERT INTO developers (name, surname, age, sex, companies_id, salary) VALUES (?,?,?,?,?,?);");
        setDevelopersProjects = getPreparedStatement("INSERT INTO developers_projects VALUES (?, ?);");
        setDevelopersSkills = getPreparedStatement("INSERT INTO developers_skills VALUES (?, ?);");
        getAllDevelopers = getPreparedStatement("SELECT * FROM developers;");
        getDeveloper = getPreparedStatement("SELECT * FROM developers WHERE id=?;");

        getProjectFromDevId = getPreparedStatement("SELECT projects.* " +
                "FROM projects " +
                "INNER JOIN developers_projects " +
                "ON developers_projects.projects_id = projects.id " +
                "WHERE developers_id = ?;");
        getSkillFromDevId = getPreparedStatement("SELECT skills.* " +
                "FROM skills " +
                "INNER JOIN developers_skills " +
                "ON developers_skills.skills_id = skills.id " +
                "WHERE developers_id = ?;");
        getCompany = getPreparedStatement("SELECT * FROM companies WHERE id = ?;");
        dropDevelopers = getPreparedStatement("DELETE FROM developers WHERE id = ?;");
        updateDevelopers = "UPDATE developers SET %s WHERE id = %s;";

    }

    @Override
    public ApiResponse save(DevelopersDto dto) {
        try{
        setDeveloper.setString(1, dto.getName());
        setDeveloper.setString(2, dto.getSurname());
        setDeveloper.setLong(3, dto.getAge());
        setDeveloper.setString(4, dto.getSex());
        setDeveloper.setLong(5, dto.getCompanyId());
        setDeveloper.setInt(6, dto.getSalary());

        if(setDeveloper.executeUpdate() > 0){
            ResultSet generatedKeys = setDeveloper.getGeneratedKeys();
            generatedKeys.next();
            long devId = generatedKeys.getLong(1);
            for (String project : dto.getProjectsList()) {
                setDevelopersProjects.setLong(1, devId);
                setDevelopersProjects.setLong(2, Long.parseLong(project));
                setDevelopersProjects.execute();
            }
            for (String skill : dto.getSkillsList()) {
                setDevelopersSkills.setLong(1, devId);
                setDevelopersSkills.setLong(2, Long.parseLong(skill));
                setDevelopersSkills.execute();
            }
            return new ApiResponse(HttpServletResponse.SC_OK, "Ваш разбатотчик успешно добавлен в базу. ID = " + devId);
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
            dropDevelopers.setLong(1,index);
            return  dropDevelopers.executeUpdate()>0 ?  OK_API_RESPONSE:FAIL_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }

    @Override
    public DevelopersOutputDto read(Long index) {
        try {
            DtoParsingService dtoParsingService = new DtoParsingService();

            getDeveloper.setLong(1, index);
            ResultSet resultSet = getDeveloper.executeQuery();
            DevelopersOutputDto developer = dtoParsingService.convertResulSetToDto(resultSet, DevelopersOutputDto.class);

            getProjectFromDevId.setLong(1, index);
            ResultSet resultSet1 = getProjectFromDevId.executeQuery();
            List<ProjectsOutputDto> projects = dtoParsingService.convertResulSetToDtoList(resultSet1, ProjectsOutputDto.class);
            developer.setProjectsList(projects);

            getSkillFromDevId.setLong(1, index);
            ResultSet resultSet2 = getSkillFromDevId.executeQuery();
            List<SkillsOutputDto> skills = dtoParsingService.convertResulSetToDtoList(resultSet2, SkillsOutputDto.class);
            developer.setSkillsList(skills);

            getCompany.setLong(1, resultSet.getLong("companies_id"));
            ResultSet resultSet3 = getCompany.executeQuery();
            CompaniesOutputDto companies = dtoParsingService.convertResulSetToDto(resultSet3, CompaniesOutputDto.class);
            developer.setCompany(companies);

            return developer;
        } catch (SQLException | JsonProcessingException e){
            e.printStackTrace();
        }
        return new DevelopersOutputDto();
    }

    @Override
    public List<DevelopersOutputDto> readAll() {
        List<DevelopersOutputDto> result = new ArrayList<>();
        try {
            ResultSet resultSet = getAllDevelopers.executeQuery();
            while (resultSet.next()){
                result.add(read(resultSet.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ApiResponse update(Long index, DevelopersDto dto) {
        StringBuilder queryBuilder = new StringBuilder();
        try {
            if (dto.getName() != null){
                queryBuilder.append("name = \"").append(dto.getName()).append("\", ");
            }
            if (dto.getAge() != null){
                queryBuilder.append("age = \"").append(dto.getAge()).append("\", ");
            }
            if (dto.getSex() != null){
                queryBuilder.append("sex = \"").append(dto.getSex()).append("\", ");
            }
            if (dto.getSalary() != null){
                queryBuilder.append("salary = ").append(dto.getSalary()).append("\", ");
            }
            if (dto.getSurname() != null){
                queryBuilder.append("surname = \"").append(dto.getSurname()).append("\", ");
            }
            if (dto.getCompanyId() != null){
                queryBuilder.append("companies_id = ").append(dto.getCompanyId()).append(", ");
            }
            if (queryBuilder.length() > 0){
                queryBuilder.deleteCharAt(queryBuilder.length()-2);
                String query = String.format(updateDevelopers, queryBuilder, index);
                dataBaseService.updateData(query);
            }
            if (dto.getProjectsList() != null){
                dataBaseService.deleteData("DELETE FROM developers_projects WHERE developers_id=" + index);
                for (String project : dto.getProjectsList()) {
                    setDevelopersProjects.setLong(1, index);
                    setDevelopersProjects.setLong(2, Long.parseLong(project));
                    setDevelopersProjects.execute();
                }
            }
            if (dto.getSkillsList() != null){
                dataBaseService.deleteData("DELETE FROM developers_skills WHERE developers_id=" + index);
                for (String skill : dto.getSkillsList()) {
                    setDevelopersSkills.setLong(1, index);
                    setDevelopersSkills.setLong(2, Long.parseLong(skill));
                    setDevelopersSkills.execute();
                }
            }
            return OK_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }
}
