package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradingDealModel {
    @JsonProperty(value = "DealID")
    private String dealID;
    @JsonProperty(value = "CardToTrade")
    private String cardToTrade;
    @JsonProperty(value = "Type")
    private String type;
    @JsonProperty(value = "MinimumDamage")
    private int minimumDamage;
}
