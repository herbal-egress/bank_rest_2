package com.example.bankcards.entity;

// добавленный код: Импорты для JPA и валидации.
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "roles", schema = "bankrest")
public class Role {

    // добавленный код: Поля id и name (Enum для ADMIN/USER).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;

    // добавленный код: Enum для ролей.
    public enum RoleName {
        ADMIN,
        USER
    }

    public Role() {
    }

    public Role(RoleName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}