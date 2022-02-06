package pl.ecookhub.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private boolean enabled = true;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private LocalDateTime deletedAt;
    private String activation;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<RefreshToken> refreshTokens;
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Authorities> authorities;
    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private ResetPassword resetPasswords;

    @PrePersist
    void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void setEditedAt() {
        this.editedAt = LocalDateTime.now();
    }
}
