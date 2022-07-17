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
public class SkillsOutputDto {
    @Id
    @GeneratedValue
    @JsonProperty(value = "id")
    private Long id;
    @JsonProperty(value = "industry")
    private String industry;
    @JsonProperty(value = "degree")
    private String degree;
}