package pl.ecookhub.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ecookhub.api.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);
}