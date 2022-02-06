package pl.ecookhub.api.model.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ResetPasswordModel {

    @Length(min = 8)
    private String password;
    @Length(min = 8)
    private String repeatPassword;
    @Length(min = 6, max = 6)
    private int token;
    private String email;
}
