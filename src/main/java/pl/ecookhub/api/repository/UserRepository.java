package pl.ecookhub.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.ecookhub.api.entity.User;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    User findByActivation(String activation);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.refreshTokens")
    Set<User> getAllUsers();
}