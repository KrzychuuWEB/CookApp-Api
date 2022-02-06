package pl.ecookhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.ecookhub.api.dto.user.UserDTO;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.factory.UserFactory;
import pl.ecookhub.api.model.user.ChangePasswordModel;
import pl.ecookhub.api.model.user.CreateResetPasswordTokenModel;
import pl.ecookhub.api.model.user.CreateUserModel;
import pl.ecookhub.api.model.user.ResetPasswordModel;
import pl.ecookhub.api.service.user.UserActivationService;
import pl.ecookhub.api.service.user.UserPasswordResetService;
import pl.ecookhub.api.service.user.UserService;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends AbstractApiController {

    private final UserService userService;
    private final UserActivationService userActivationService;
    private final UserPasswordResetService userPasswordResetService;

    @GetMapping
    @Secured("ROLE_USER")
    public ResponseEntity<Object> getAllUsers() {
        Set<User> users = userService.getAllUsers();

        return jsonResponse("Users list", users.stream().map(UserDTO::new).collect(Collectors.toSet()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        User user = userService.getUserById(id);

        return jsonResponse("User by id: "+ id, new UserDTO(user), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserModel createUserModel) {
        UserFactory userFactory = new UserFactory();
        String username = userService.createUser(userFactory.create(createUserModel));

        return jsonResponse("User " + username + " has been created!", null, HttpStatus.CREATED);
    }

    @GetMapping("/activate")
    public ResponseEntity<Object> activateUser(@RequestParam String key) {
        return jsonResponse("Account activate", userActivationService.activateAccount(key), HttpStatus.OK);
    }

    @PutMapping("/password/change")
    @Secured("ROLE_USER")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordModel changePasswordModel, Authentication authentication) {
        if (!Objects.equals(changePasswordModel.getNewPassword(), changePasswordModel.getRepeatPassword())) {
            return jsonResponse("Password is not same", null, HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserByEmail(authentication.getName());
        userService.updatePassword(user, changePasswordModel.getCurrentPassword(), changePasswordModel.getNewPassword());

        return jsonResponse("Password has been changed", null, HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Object> generateResetPasswordToken(@RequestBody CreateResetPasswordTokenModel resetPasswordModel) {
        userPasswordResetService.generateNewResetPasswordTokenAndSendMail(resetPasswordModel.getEmail());
        return jsonResponse("You have mail with your reset password token", null, HttpStatus.OK);
    }

    @PutMapping("/password/reset")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordModel resetPasswordModel) {
        if (!Objects.equals(resetPasswordModel.getPassword(), resetPasswordModel.getRepeatPassword())) {
            return jsonResponse("Passwords is not same", null, HttpStatus.BAD_REQUEST);
        }

        userPasswordResetService.resetPassword(resetPasswordModel);

        return jsonResponse("Password changed", null, HttpStatus.OK);
    }
}
