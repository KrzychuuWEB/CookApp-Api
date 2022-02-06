package pl.ecookhub.api.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateResetPasswordTokenModel {

    private String email;
}
