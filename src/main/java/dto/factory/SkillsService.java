package dto.factory;

import dto.model.SkillsDto;
import dto.model.SkillsOutputDto;
import dto.service.DtoService;
import util.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SkillsService extends DtoFactory<SkillsDto, SkillsOutputDto> {
    private final PreparedStatement setSkill;
    private final PreparedStatement dropSkill;
    private final PreparedStatement getSkill;
    private final PreparedStatement getAllSkills;
    private final String updateSkill;

    {
        setSkill = getPreparedStatement("INSERT INTO skills (industry, degree) VALUES (?,?);");
        dropSkill = getPreparedStatement("DELETE FROM skills WHERE id = ?;");
        getSkill = getPreparedStatement("SELECT * FROM skills WHERE id = ?;");
        getAllSkills = getPreparedStatement("SELECT * FROM skills;");
        updateSkill = "UPDATE skills SET %s WHERE id = %s;";
    }

    @Override
    public ApiResponse save(SkillsDto dto) {
        try{
            System.out.println("dto.toString() = " + dto.toString());
            setSkill.setString(1, dto.getIndustry());
            setSkill.setString(2, dto.getDegree());

            if (setSkill.executeUpdate()>0){
                ResultSet generatedKeys = setSkill.getGeneratedKeys();
                generatedKeys.next();
                long skiId = generatedKeys.getLong(1);
                return new ApiResponse(HttpServletResponse.SC_OK, "Ваш скил успешно добавлен в базу. ID = " + skiId);
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
            dropSkill.setLong(1, index);
            return  dropSkill.executeUpdate()>0 ?  OK_API_RESPONSE:FAIL_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }

    @Override
    public SkillsOutputDto read(Long index) {
        try {
            DtoService dtoService = new DtoService();
            getSkill.setLong(1, index);
            ResultSet resultSet = getSkill.executeQuery();
            return dtoService.convertResulSetToDto(resultSet, SkillsOutputDto.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SkillsOutputDto();
    }

    @Override
    public List<SkillsOutputDto> readAll() {
        List<SkillsOutputDto> result = new ArrayList<>();
        try {
            ResultSet resultSet = getAllSkills.executeQuery();
            while (resultSet.next()){
                result.add(read(resultSet.getLong("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ApiResponse update(Long index, SkillsDto dto) {
        StringBuilder queryBuilder = new StringBuilder();
        try{
            if (dto.getDegree() != null){
                queryBuilder.append("degree = \"").append(dto.getDegree()).append("\", ");
            }
            if (dto.getIndustry() != null){
                queryBuilder.append("industry = \"").append(dto.getIndustry()).append("\", ");
            }

            if (queryBuilder.length() > 0){
                queryBuilder.deleteCharAt(queryBuilder.length()-2);
                String query = String.format(updateSkill, queryBuilder, index);
                dataBaseService.updateData(query);
            }
            return OK_API_RESPONSE;
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL_API_RESPONSE;
        }
    }
}