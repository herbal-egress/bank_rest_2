package com.example.bankcards.entity;
import com.example.bankcards.util.PasswordConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "users", schema = "bankrest")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(disableConversion = true)
    private Long id;
    @NotNull
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    @Column(name = "username", nullable = false, unique = true)
    @Convert(disableConversion = true)
    private String username;
    @NotNull
    @Size(min = 3, max = 100, message = "Пароль должен быть от 3 до 100 символов")
    @Column(name = "password", nullable = false)
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "bankrest",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Convert(disableConversion = true)
    private Set<Role> roles = new HashSet<>();
    public User() {
    }
    public User(String username, String password, Set<Role> roles) {
        this.username = username;
        this.password = password;
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
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}