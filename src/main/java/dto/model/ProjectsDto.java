package dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectsDto {
    private String name;
    private Long budget;
    private String releaseDate;
    private Long companyId;
    private Long customerId;
}