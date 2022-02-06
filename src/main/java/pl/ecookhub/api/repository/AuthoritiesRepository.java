package pl.ecookhub.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ecookhub.api.entity.Authorities;

public interface AuthoritiesRepository extends JpaRepository<Authorities, Long> {
}