package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsModel {
    @JsonProperty(value = "Position")
    private Integer position;
    @JsonProperty(value = "Name")
    private String username;
    @JsonProperty(value = "Elo")
    private Integer elo;
    @JsonProperty(value = "Wins")
    private Integer wins;
    @JsonProperty(value = "Losses")
    private Integer losses;
}
