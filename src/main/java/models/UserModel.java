package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserModel {
    @JsonProperty(value = "Username")
    private String username;
    @JsonProperty(value = "Password")
    private String password;
    @JsonProperty(value = "Authorization")
    private String authorization;
    @JsonProperty(value = "Name")
    private String newUsername;
    @JsonProperty(value = "Bio")
    private String newBio;
    @JsonProperty(value = "Image")
    private String newImage;
    @JsonProperty(value = "Id")
    private String cardID;
}

