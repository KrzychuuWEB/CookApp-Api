package pl.ecookhub.api.factory;

import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.model.user.CreateUserModel;

public class UserFactory {

    public User create(CreateUserModel createUserModel) {
        User user = new User();
        user.setUsername(createUserModel.getUsername());
        user.setPassword(createUserModel.getPassword());
        user.setEmail(createUserModel.getEmail());

        return user;
    }
}
