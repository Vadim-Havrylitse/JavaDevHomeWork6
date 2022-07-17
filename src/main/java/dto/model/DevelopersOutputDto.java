package dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Data
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevelopersOutputDto {
    @Id
    @GeneratedValue
    @JsonProperty(value = "id")
    private Long id;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "surname")
    private String surname;
    @JsonProperty(value = "age")
    private Long age;
    @JsonProperty(value = "sex")
    private String sex;
    @JsonProperty(value = "salary")
    private Integer salary;
    private List<SkillsOutputDto> skillsList;
    private List<ProjectsOutputDto> projectsList;
    private CompaniesOutputDto company;
}