package dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompaniesOutputDto {
    @Id
    @GeneratedValue
    @JsonProperty(value = "id")
    private Long id;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "country")
    private String country;
    @JsonProperty(value = "city")
    private String city;
}