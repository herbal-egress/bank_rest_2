package com.example.bankcards.entity;

// добавленный код: Импорты для JPA, валидации, связи с Role, хеширования пароля.
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", schema = "bankrest")
public class User {

    // добавленный код: Поля согласно ТЗ (id, username, password, email) + связь с Role.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    @Column(name = "password", nullable = false)
    private String password; // Хешируется через BCrypt в сервисе.

    @NotNull
    @Email(message = "Некорректный формат email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // добавленный код: Связь @ManyToMany с Role для ролей ADMIN/USER.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "bankrest",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String username, String password, String email, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}