package pl.ecookhub.api.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class CreateUserModel {

    private String username;
    private String password;
    @Email
    private String email;
}
