package pl.ecookhub.api.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordModel {

    private String currentPassword;
    private String newPassword;
    private String repeatPassword;
}
