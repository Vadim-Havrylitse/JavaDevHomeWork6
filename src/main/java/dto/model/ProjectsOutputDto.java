package dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectsOutputDto {
    @Id
    @GeneratedValue
    @JsonProperty(value = "id")
    private Long id;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "budget")
    private Long budget;
    @JsonProperty(value = "release_date")
    private String releaseDate;
    private CompaniesOutputDto company;
    private CustomersOutputDto customer;
}