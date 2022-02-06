package pl.ecookhub.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ecookhub.api.entity.ResetPassword;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    ResetPassword findByToken(int token);
    void deleteByUserId(Long userId);
}