package pl.ecookhub.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reset_passwords")
@Getter
@Setter
public class ResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(unique = true)
    private int token;
    private LocalDateTime expiryDate;

    @PrePersist
    void expiryDate() {
        this.expiryDate = LocalDateTime.now().plusDays(1);
    }
}
