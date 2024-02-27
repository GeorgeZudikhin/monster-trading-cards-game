package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardModel {
    @JsonProperty(value = "Id")
    private String cardID;
    @JsonProperty(value = "Name")
    private String cardName;
    @JsonProperty(value = "Element")
    private String cardElement;
    @JsonProperty(value = "Damage")
    private int cardDamage;
    @JsonProperty(value = "PackageID")
    private int packageID;
}
