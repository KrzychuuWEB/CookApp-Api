package pl.ecookhub.api.objectMother;

import pl.ecookhub.api.entity.User;

import java.util.*;
import java.util.stream.Collectors;

public class UserBuilder {

    private static final long DEFAULT_ID = 1;
    private static final String DEFAULT_USERNAME = "test_username";
    private static final String DEFAULT_PASSWORD = "test_password";
    private static final String DEFAULT_EMAIL = "test@email.com";
    private static final boolean DEFAULT_ENABLED = true;
    private static final String DEFAULT_ACTIVATION_KEY = "test_activation_key";

    private long id = DEFAULT_ID;
    private String username = DEFAULT_USERNAME;
    private String password = DEFAULT_PASSWORD;
    private String email = DEFAULT_EMAIL;
    private boolean isEnabled = DEFAULT_ENABLED;
    private String activation = DEFAULT_ACTIVATION_KEY;

    private UserBuilder() {
    }

    public static UserBuilder newUser() {
        return new UserBuilder();
    }

    public UserBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withEnabled(boolean enabled) {
        this.isEnabled = enabled;
        return this;
    }

    public Set<User> usersSetList() {
        List<User> users = new ArrayList<>();
        users.add(build());

        User user2 = build();
        user2.setUsername("test_username_1");
        users.add(user2);

        return users.stream().sorted(Comparator.comparing(User::getUsername)).collect(Collectors.toSet());
    }

    public UserBuilder but() {
        return UserBuilder
                .newUser()
                .withId(id)
                .withUsername(username)
                .withPassword(password)
                .withEmail(email)
                .withEnabled(isEnabled);
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setEnabled(isEnabled);
        user.setActivation(activation);

        return user;
    }
}
