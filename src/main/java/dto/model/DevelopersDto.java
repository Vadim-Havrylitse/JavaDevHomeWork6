package dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevelopersDto {
    private String name;
    private String surname;
    private Long age;
    private String sex;
    private Integer salary;
    private List<String> skillsList;
    private List<String> projectsList;
    private Long companyId;
}