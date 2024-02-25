package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingDealModel {
    @JsonProperty(value = "Id")
    private String Id;
    @JsonProperty(value = "CardToTrade")
    private String cardToTrade;
    @JsonProperty(value = "Type")
    private String type;
    @JsonProperty(value = "MinimumDamage")
    private int minimumDamage;
    @JsonProperty(value = "UserID")
    private int userId;
}
